package itman.com.cmmn.util.file;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * @comment: 폴더를 감시하고 동기화를 진행하는 클래스
 * @usage: FolderSyncWatcher.builder()
                                    .setSrcFolderPath(소스 폴더 경로)
                                    .setDestFolderPath(목적지 폴더 경로)
                                    .setThreadCount(스레드 갯수)
                                    .build()
                                    .startMonitoring();
 * @author: ybwhyb
 * @since: 2024-01-20
 * */

@Slf4j
public class FileSyncWatcherService {

    final Path sourceFolderPath;
    final Path destinationFolderPath;
    final WatchService watchService;
    final ExecutorService executorService;
    final Lock lock;

    private FileSyncWatcherService(Path sourceFolderPath, Path destinationFolderPath, int count) throws IOException {
        this.sourceFolderPath = sourceFolderPath;
        this.destinationFolderPath = destinationFolderPath;
        this.watchService = FileSystems.getDefault().newWatchService();
        this.executorService = Executors.newFixedThreadPool(count);
        this.lock = new ReentrantLock(true);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * @comment: 폴더 이벤트를 감시
     * @param: void
     * @return: void
     * */
    public void startMonitoring() {
        try {
            synchronizeFolders(sourceFolderPath.toFile(), destinationFolderPath.toFile());
            // 생성, 삭제, 수정 이벤트 등록
            sourceFolderPath.register(watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE
            );

            //System.out.println("폴더 감시를 시작합니다...");
            log.info(":: 폴더 감시를 시작합니다...");

            executorService.submit(() -> {
                try {
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            executorService.submit(() -> handleEvent(event));
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println(e.getMessage());
                }
            });

        } catch (IOException e) {
            //System.err.println("폴더 감시 중 오류 발생: " + e.getMessage());
            log.error("폴더 감시 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * @comment:  이벤트 처리
     * @param: WatchEvent
     * @return: void
     * */
    private void handleEvent(WatchEvent<?> event) {
        WatchEvent.Kind<?> kind = event.kind();
        Path changedPath = (Path) event.context();

        try{
            lock.lock();
            if (kind == StandardWatchEventKinds.ENTRY_MODIFY || kind == StandardWatchEventKinds.ENTRY_CREATE) {
                synchronizeFolders(sourceFolderPath.resolve(changedPath).toFile(), destinationFolderPath.resolve(changedPath).toFile());
                //System.out.println("::>> eventType: "+kind.name());
                log.info("::>> eventType : "+kind.name());
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                log.info("::>> eventType : "+kind.name());
                //System.out.println("::>> eventType: "+kind.name());
                FileUtils.deleteQuietly(destinationFolderPath.resolve(changedPath).toFile());
            }
        }catch (Exception e){
            log.error(":: ERR: "+e.getMessage());
            //System.err.println(e.getMessage());
        }finally {
            lock.unlock();
        }
    }

    /**
     * @comment: 폴더 동기화
     * @param: File (원본 )
     * @parma: File (목적지)
     * @return: void
     * */
    private void synchronizeFolders(File source, File destination){
        lock.lock();
        // 대상 폴더가 존재하지 않으면 생성

        try{
            if (!destination.exists()) {
                destination.mkdirs();
            }

            // 소스 폴더 내의 파일 및 폴더 목록을 가져옴
            File[] sourceFiles = source.listFiles();
            Set<String> sourceFileNames = new HashSet<>();

            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    File destinationFile = new File(destination, sourceFile.getName());
                    sourceFileNames.add(sourceFile.getName());
                    if (sourceFile.isDirectory()) {
                        synchronizeFolders(sourceFile, destinationFile);
                    } else {
                        FileUtils.copyFile(sourceFile, destinationFile);
                    }
                }
            }
            // 삭제
            File[] destinationFiles = destination.listFiles();
            if (destinationFiles != null) {
                for (File destinationFile : destinationFiles) {
                    if (!sourceFileNames.contains(destinationFile.getName())) {
                        // 소스 폴더에 해당 파일 또는 폴더가 없으면 삭제
                        if (destinationFile.isDirectory()) {
                            FileUtils.deleteDirectory(destinationFile);
                        } else {
                            FileUtils.deleteQuietly(destinationFile);
                        }
                        log.info(":: 삭제: " + destinationFile);

                    }
                }
            }
        }catch (Exception e){
            //System.err.println("::>> ERR: "+e.getCause());
            log.info("::>> ERR: "+e.getCause());
        } finally {
            lock.unlock();
        }

    }

    public static class Builder {

        private Path sourceFolderPath;
        private Path destinationFolderPath;
        private int threadCount;

        /**
         * @commnet: 소스폴더 경로 설정
         * @param: String
         * @return: Builder
         * */
        public Builder setSrcFolderPath(String sourceFolderPath) {
            this.sourceFolderPath = Paths.get(sourceFolderPath);
            return this;
        }

        /**
         * @commnet: 목적지폴더 경로 설정
         * @param: String
         * @return: Builder
         * */
        public Builder setDestFolderPath(String destinationFolderPath) {
            this.destinationFolderPath = Paths.get(destinationFolderPath);
            return this;
        }

        /**
         * @commnet: 스레드 갯수 설정
         * @param: int
         * @return: Builder
         * */
        public Builder setThreadCount(int threadCount){
            this.threadCount = threadCount;
            return this;
        }

        /**
         * @comment: FolderSyncWatcherService 빌더 생성
         * @param: void
         * @return: FolderSyncWatcherService
         * */
        public FileSyncWatcherService build() throws IOException {
            if (sourceFolderPath == null || destinationFolderPath == null) {
                throw new IllegalArgumentException("소스 폴더 경로나 목적지 폴더의 경로가 NULL ");
            }
            return new FileSyncWatcherService(sourceFolderPath, destinationFolderPath, threadCount);
        }
    }

}