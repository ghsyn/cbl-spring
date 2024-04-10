package itman.com.cmmn.util.file.reader;

import itman.com.cmmn.util.BitConverter;
import lombok.Builder;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

@Builder
public class PDQReader implements AutoCloseable {

    final DataInputStream dis;
    @Builder.Default
    final int STEP = 32;

//    private PDQReader(String fileNm, DataInputStream dis) throws IOException{
//        this.fileNm = fileNm;
//        //this.dis = new DataInputStream(new FileInputStream(fileNm));
//        this.dis = dis;
//    }
//
//    public static Builder newBuiler(String fileNm){
//        return new Builder(fileNm);
//    }

    /**
     * @comment: {key1:[data1 list], key2:[data2 list], key3: [data3 list]} 형태로 만들기 위한함수
     * @parma: void
     * @return: Map {key1:[data1 list], key2:[data2 list], key3: [data3 list]} 형태의 데이터
     * */
    public Map<String, List<?>> result2Map() throws Exception{
        byte[] rawData = readPDQFile();

        if(rawData == null) {
            throw  new IOException("Error reading PDQ file");
        }

        List<Object> pList = new ArrayList<>();
        List<Object> pCalcList = new ArrayList<>();
        List<Object> degList = new ArrayList<>();

        float oPulse = 0; // original pulse
        short deg = 0; // degree

        for(int i = 0; i < rawData.length; i += 10){
            oPulse = BitConverter.toSingle(rawData, i);
            pList.add(oPulse);
            pCalcList.add((int)(Math.abs(oPulse) / STEP));

            if(oPulse == 0) break;
            deg = BitConverter.toInt16(rawData, i+4);
            degList.add((int)deg * 360 / 1024);
        }

        Map<String,List<?>> result = new HashMap<>();
        result.put("originPulse", pList);
        result.put("calcPulse", pCalcList);
        result.put("degList", degList);

        return result;
    }

    public List<Map<String,Object>> result2List() throws Exception{
        byte[] rawData = readPDQFile();

        if(rawData == null) {
            throw  new IOException("Error reading PDQ file");
        }
        List<Map<String,Object>> result = new ArrayList<>();
        float oPulse = 0; // original pulse
        short deg = 0; // degree

        for(int i = 0; i < rawData.length; i += 10){
            oPulse = BitConverter.toSingle(rawData, i);
            LinkedHashMap map = new LinkedHashMap();
            map.put("originPulse", oPulse);
            map.put("calcPuluse", (int)(Math.abs(oPulse) / STEP));
            map.put("degList", (BitConverter.toInt16(rawData, i+4) * 30 / 1024));
            result.add(map);
            if(oPulse == 0) break;
        }

        return result;
    }



    /**
     * @comment: Binary 파일을 읽기 위한 함수
     * @param: void
     * @return: byte[] Binary 파일을 읽어들인 데이터
     * */
    private synchronized byte[] readPDQFile() throws Exception{
        try{
            byte[] buf = new byte[dis.available()];
            dis.readFully(buf);
            return buf;
        }catch (IOException e){
            throw new IllegalArgumentException("Error reading PDQFile"+ e);
        }

    }

    @Override
    public void close() throws Exception {
        if(dis != null) dis.close();
    }

//    public static class Builder{
//        private String fileNm;
//        private Builder(String fileNm){
//            this.fileNm = fileNm;
//        }
//
//        public PDQReader build() throws IOException{
//            return new PDQReader(fileNm, new DataInputStream(Files.newInputStream(Paths.get(fileNm))));
//        }
//    }
}
