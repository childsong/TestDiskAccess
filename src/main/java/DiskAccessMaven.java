import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.*;

public class DiskAccessMaven {
    public static void main(String[] args) throws IOException {
        String path=new String("src/main/");
        File file=new File(path);
        File deleteFilePath=new File("src/main/db");
        deleteFile(deleteFilePath);
        try {
            DB db=Iq80DBFactory.factory.open(new File(file,"db"),new Options().createIfMissing(true));

            long startTime;
            long endTime;
            long dataSize=100000;
            int seqSize=100;

            /*随机写的时间*/
            startTime=System.currentTimeMillis();
            randomWrite(db,dataSize);
            endTime=System.currentTimeMillis();
            System.out.println("randomWrite:"+(endTime-startTime));


            /*随机读的时间*/
            startTime=System.currentTimeMillis();
            randomRead(db,dataSize);
            endTime=System.currentTimeMillis();
            System.out.println("randomRead:"+(endTime-startTime));


            /*顺序写时间，部分顺序*/
            startTime=System.currentTimeMillis();
            sequentialWrite(db,dataSize,seqSize);
            endTime=System.currentTimeMillis();
            System.out.println("sequentialWrite:"+(endTime-startTime));


            /*顺序读时间，部分顺序*/
            startTime=System.currentTimeMillis();
            sequentialRead(db,dataSize,seqSize);
            endTime=System.currentTimeMillis();
            System.out.println("SequentialRead:"+(endTime-startTime));



            /*下面是随机写，但是是object类型的*/

            startTime=System.currentTimeMillis();
            objectRandomWrite(db,dataSize);
            endTime=System.currentTimeMillis();
            System.out.println("objectRandomWrite:"+(endTime-startTime));



            /*下面是object的随机读*/
            startTime=System.currentTimeMillis();
            objectRandomRead(db,dataSize);
            endTime=System.currentTimeMillis();
            System.out.println("objectRandomRead:"+(endTime-startTime));


            /*下面是object的顺序写*/
            startTime=System.currentTimeMillis();
            objectSeqWrite(db,dataSize,seqSize);
            endTime=System.currentTimeMillis();
            System.out.println("objectSeqWrite:"+(endTime-startTime));


            /*下面是object的顺序读*/
            startTime=System.currentTimeMillis();
            objectSeqRead(db,dataSize,seqSize);
            endTime=System.currentTimeMillis();
            System.out.println("objectSeqRead:"+(endTime-startTime));


            db.close();


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    public static byte[] toByteArray (Object object) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }



    public static void randomWrite(DB db,long dataSize){
        for(int i=0;i<dataSize;i++){
            String str=new String(i+"");
            String str2=new String(2.0f+"");
            db.put(str.getBytes(),str2.getBytes());
        }
    }

    public static void randomRead(DB db,long dataSize){
        for(int i=0;i<dataSize;i++){
            String a=new String(db.get((i+"").getBytes()));
        }
    }

    public static void sequentialWrite(DB db,long dataSize,int seqSize){
        for(int i=0;i<dataSize;i=i+seqSize){
            String str=new String("seq"+i/seqSize);
            String str2=new String();
            for(int j=0;j<seqSize;j++){
                str2+=2.0f;
            }
            db.put(str.getBytes(),str2.getBytes());
        }
    }

    public static void sequentialRead(DB db,long dataSize,int seqSize){
        for(int i=0;i<dataSize;i=i+seqSize){
            String a=new String(db.get(("seq"+i/seqSize).getBytes()));
        }
    }
    public static void objectRandomWrite(DB db,long dataSize){
        for(int i=0;i<dataSize;i++) {
            Parameter parameter = new Parameter();
            parameter.key = i;
            parameter.value = 1.0f;
            byte[] btArray = toByteArray(parameter);
            db.put(("para" + i).getBytes(), btArray);
        }
    }
    public static void objectRandomRead(DB db,long dataSize){
        for(int i=0;i<dataSize;i++){
            Parameter parameter=new Parameter();
            byte[] bt=db.get(("para"+i).getBytes());
            parameter=(Parameter) parameter.toObject(bt);

        }
    }

    public static void objectSeqWrite(DB db,long dataSize,int seqSize){
        for(int i=0;i<dataSize;i=i+seqSize){
            ParameterList parameterList=new ParameterList();
            for(int j=i;j<i+seqSize;j++){
                Parameter parameter=new Parameter();
                parameter.key=j;
                parameter.value=1.0f;
                parameterList.parameterList.add(parameter);
            }
            byte[] bytes=toByteArray(parameterList);
            db.put(("paraL"+i/seqSize).getBytes(),bytes);
        }
    }

    public static void objectSeqRead(DB db,long dataSize,int seqSize){
        for(int i=0;i<dataSize/seqSize;i++){
            byte[] bytes=db.get(("paraL"+i/seqSize).getBytes());
            ParameterList parameterList=new ParameterList();
            parameterList=(ParameterList) parameterList.toObject(bytes);

        }
    }
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

}
