package org.dodo.register.center;

import org.dodo.common.spi.Spi;
import org.dodo.register.URL;
import org.dodo.register.discovery.DiscoveryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * 为方便测试，监听本地文件的变化
 * @author maxlim
 */
@Spi("local4test")
public class Local4TestRegister implements Register {
    private Logger logger = LoggerFactory.getLogger(Local4TestRegister.class);
    private WatchService watcher;
    private Thread listenerThread;
    private volatile boolean listenerStopFlag;
    private final static String FILE_NAME = "local4test.register";
    private List<DiscoveryListener> discoveryListeners = new ArrayList<>();

    public void start() throws IOException {
        if(watcher == null) {
            synchronized (this) {
                if(watcher == null) {
                    String filepath = System.getProperty("java.io.tmpdir")+"/dodo";
                    if(Files.notExists(Paths.get(filepath))) Files.createDirectory(Paths.get(filepath));
                    if(Files.notExists(Paths.get(filepath+"/"+FILE_NAME))) Files.createFile(Paths.get(filepath+"/"+FILE_NAME));
                    if(logger.isDebugEnabled()) {
                        logger.debug("dodo local4test register:" + filepath+"/"+FILE_NAME);
                    }

                    watcher = FileSystems.getDefault().newWatchService();
                    Paths.get(filepath).register(watcher, ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);

                    listenerThread = new Thread(()->listenFiles());
                    listenerThread.start();
                }
            }
        }
    }

    private void listenFiles() {
        while(true){
            try {
                WatchKey key = watcher.take();
                for(WatchEvent<?> event : key.pollEvents()){
                    WatchEvent.Kind kind = event.kind();
                    if(kind == OVERFLOW){
                        continue;
                    }
                    WatchEvent<Path> e = (WatchEvent<Path>)event;
                    if(FILE_NAME.equals(e.context().getFileName())) {
                        getData();
                    }
                }
                if( ! key.reset()){
                    Thread.sleep(3000L);
                }
                if(listenerStopFlag) break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    private void getData() {
        String filepath = System.getProperty("java.io.tmpdir") +"/dodo/"+ FILE_NAME;
        try {
            List<String> lines = Files.readAllLines(Paths.get(filepath));
            lines.forEach(line -> {
                for (DiscoveryListener discoveryListener : discoveryListeners) {
                    discoveryListener.notifyNew(URL.build(line));
                }
            });
        } catch (IOException e) {
            logger.error(null, e);
        }
    }

    @Override
    public void register(URL url) throws Exception {
        start();

        Files.write(Paths.get(System.getProperty("java.io.tmpdir") +"/dodo/"+ FILE_NAME), url.toString().getBytes());
    }

    @Override
    public void unregister(URL url) throws Exception {
        start();
    }

    @Override
    public void subscribe(String service, DiscoveryListener listener) throws Exception {
        start();

        discoveryListeners.add(listener);

        getData();
    }

    @Override
    public void close() throws IOException {
        listenerStopFlag = true;
        if(watcher != null) watcher.close();
    }
}
