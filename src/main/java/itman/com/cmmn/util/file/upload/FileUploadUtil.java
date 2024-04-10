package itman.com.cmmn.util.file.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * @comment:
 * @author: ybwhyb
 * @since: 2024-01-23
 *
 * */
@Slf4j
public class FileUploadUtil {

    @Value("${file.upload.path}")
    String savePath;
    long maxSize;
    List<String> extList;

    private FileUploadUtil(String savePath, List<String> extList, long maxSize){
        this.savePath = savePath;
        this.extList = extList;
        this.maxSize = maxSize;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    /**
     * @comment: 파일 업로드 함수
     * @param: MultipartFile 업로드할 파일
     * @return: ResponseEntity 성공 or 실패 메시지를 전달
     * */
    public ResponseEntity<String> storeFile(MultipartFile file) {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = StringUtils.getFilenameExtension(fileName);

        if (isInvalidFileExtension(fileExtension)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("유효하지 않은 확장자 유형: " + fileExtension);
        }

        if (isValidFileSize(file.getSize())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("파일 최대 허용 용량 초과");
        }

        try {
            return saveFile(file, fileName);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일을 저장할 수 없습니다. " + fileName + ". 다시 시도해 주세요!");
        }
    }


    /**
     * @comment: 파일확장자 허용 리스트 체크
     * @param: String 파일 확장
     * @return: boolean true or false
     * */
    private boolean isInvalidFileExtension(String fileExtension) {
        return !extList.contains(fileExtension.toLowerCase());
    }

    /**
     * @comment: 파일용량을 체크
     * @parma: long 파일사이즈
     * @return: boolean true or false 반환
     * */
    private boolean isValidFileSize(long fileSize) {
        return fileSize > maxSize;
    }

    /**
     * @comment: json형태로 리턴할지 String형태로 리턴할지 결정하지 않음
     * @param: MultipartFile 업로드 파일
     * @param: String 파일명
     * @return: ResponseEntity String or Json 타입의 성공여부 반환
     * */
    private ResponseEntity<String> saveFile(MultipartFile file, String originalFileName)  {
        String fileName = StringUtils.cleanPath(originalFileName);
        Path uploadDir = Paths.get(savePath).toAbsolutePath().normalize();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try{
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path targetLocation = generateUniqueFileName(uploadDir, fileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation);
            }

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body("파일 업로드 성공: " + targetLocation.getFileName());

        }catch (Exception e){
            log.error("::ERR::"+e.getMessage());
            return
                    ResponseEntity
                            .ok()
                            .headers(headers)
                            .body("파일 업로드 실패: " +e.getMessage());
        }
    }

    /**
     * @commnet: 동일파일명의 중복을 피하기 위해서 파일명__숫자 형식으로 파일 생성
     * @param: Path upload할 파일 경로
     * @parma: String fileName 파일명
     * @return: Path
     * */
    private Path generateUniqueFileName(Path uploadDir, String fileName) {
        Path targetLocation = uploadDir.resolve(fileName);

        int counter = 1;
        String baseName = null;
        String extension = null;
        while (Files.exists(targetLocation)) {
            baseName = StringUtils.stripFilenameExtension(fileName);
            extension = StringUtils.getFilenameExtension(fileName);
            String numberedFileName = baseName + "__" + counter + "." + extension;
            targetLocation = uploadDir.resolve(numberedFileName);
            counter++;
        }

        return targetLocation;
    }

    public static class Builder{
        private String savePath;
        private List<String> extList;
        private long maxSize;

        public Builder setSavePath(String savePath){
            this.savePath = savePath;
            return this;
        }

        public Builder setExtensionList(List<String> extList){
            this.extList = extList;
            return this;
        }

        public Builder setMaxFileSize(long maxSize){
            this.maxSize = maxSize;
            return this;
        }

        public FileUploadUtil build(){
            if(savePath==null) throw new IllegalArgumentException("파일 저장 경로 NULL ");
            if(extList == null) throw new IllegalArgumentException("파일 확장자 리스트 NULL");

            return  new FileUploadUtil(savePath, extList, maxSize);
        }
    }


}
