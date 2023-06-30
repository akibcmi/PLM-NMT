import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class bpe {
    public static class word{
        public String w;
        public bpeword bpe;
        public bpeword sw;
    }
    public static class bpeword{
        public String w;
    }
    public static class sentence{
        public ArrayList<word> word;
        public ArrayList<bpeword> bpeword;
        public ArrayList<bpeword> swword;
        public String sentence;
        public String sentencebpeword;
        public String sentenceswword;
    }
    public static class Sub{
        static HashMap<String,Sub> sublistBPE = new HashMap<String, Sub>();
        static HashMap<String,Sub> sublistSW = new HashMap<String, Sub>();
        static HashMap<Integer,String> idlist = new HashMap<Integer, String>();

        static void add(String bpe,String sw,double weight){
            if (sublistBPE.get(bpe).weight.containsKey(sw) == false){
                sublistBPE.get(bpe).weight.put(sw,weight);
            }
            else{
                sublistBPE.get(bpe).weight.put(sw,sublistBPE.get(bpe).weight.get(sw)+weight);
            }
        }
        HashMap<String, Double> weight;
        String word;
        HashMap<String,Integer> word2id;
        int wordid;
        public Sub(String word,int id){
            this.word = word;
            this.wordid = id;

            this.word2id = new HashMap<String, Integer>();
            this.weight = new HashMap<String, Double>();

        }
        public static void sentence(sentence sentence){
            ArrayList<ArrayList<String>> arrbpe = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> arrsw = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> arrwordbpe = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> arrwordsw = new ArrayList<ArrayList<String>>();
            String s = "";
            String sbpeword = "";
            ArrayList<String> sarrbpe = new ArrayList<String>();
            for(int j = 0  ; j < sentence.bpeword.size(); j = 1+j) {
                sbpeword = sentence.bpeword.get(j).w;
                if (sbpeword.endsWith("@@")) {
                    sarrbpe.add(sbpeword);
                } else {
                    sarrbpe.add(sbpeword);
                    arrbpe.add(sarrbpe);
                    sarrbpe = new ArrayList<String>();
                }
            }
            ArrayList<String>sarrsw=new ArrayList<String>();
            for(int j = 0 ; j < sentence.swword.size() ; j = j+1) {
                sbpeword = sentence.swword.get(sentence.swword.size() - j-1).w;
                if (sbpeword.startsWith("##"))
                    sarrsw.add(0,sbpeword);
                else{
                    sarrsw.add(0,sbpeword);
                    arrsw.add(0,sarrsw);
                    sarrsw=new ArrayList<String>();
                }
            }
            String swword="",swbpe="",swsw="";
            int jbpe=0;
            int jsw=0;
            int jwbpe=0;
            int jwsw=0;
            ArrayList<String>wordarrsw = new ArrayList<String>();
            ArrayList<String>wordarrbpe = new ArrayList<String>();
            while( jbpe <arrbpe.size() && arrsw.size()> jsw ) {
                ArrayList<String> lbpe = arrbpe.get(jbpe);
                ArrayList<String> lsw = arrsw.get(jsw);

                if (lsw.get(0).equals("[UNK]")) {
                    lsw = lbpe;
                }
                while(jwbpe<lbpe.size()){
                    swbpe = swbpe+ lbpe.get(jwbpe).replace("##","").replace("@@","").replace("@-@","-");
                    wordarrbpe.add(lbpe.get(jwbpe));
                    jwbpe =1+jwbpe;
                }
                while(lsw.size() > jwsw){
                        swsw = swsw+lsw.get(jwsw).replace("@@","").replace("##","");
                        wordarrsw.add(lsw.get(jwsw));
                        jwsw = jwsw+1;
                }

                if(swbpe.length() == swsw.length()) {
                    arrwordsw.add(wordarrsw);
                    arrwordbpe.add(wordarrbpe);
                    wordarrsw = new ArrayList<String>();
                    wordarrbpe = new ArrayList<String>();
                    jbpe=jbpe+1;
                    jsw=jsw+1;
                    jwbpe=0;
                    jwsw=0;
                } else if (swbpe.length() < swsw.length()) {
                    jwbpe=0;
                    jbpe=jbpe+1;
                } else {
                    jwsw=0;
                    jsw=jsw+1;
                }
            }
            if (arrwordbpe.size() - arrwordsw.size() != 0) {
                System.out.println(swbpe);
                System.out.println(swsw);
                System.out.println(arrwordbpe);
                System.out.println(arrwordsw);
                System.out.println(sentence.sentence);
                System.out.println(sentence.sentencebpeword);
                System.out.println(sentence.sentenceswword);

            } else {
                for (int l =    0 ; l < arrwordbpe.size() ; l = 1+l) {

                    wordarrbpe = arrwordbpe.get(l);
                    wordarrsw = arrwordsw.get(l);
                    if(wordarrbpe.size() == 1) {
                        for(String swl:wordarrsw){
                            if (Sub.sublistBPE.get(wordarrbpe.get(0)).weight.containsKey(swl) == false)
                                Sub.sublistBPE.get(wordarrbpe.get(0)).weight.put(swl,0.0);
                            Sub.sublistBPE.get(wordarrbpe.get(0)).weight.put(swl,Sub.sublistBPE.get(wordarrbpe.get(0)).weight.get(swl)+1);
                        }
                    } else if (wordarrsw.size() == 1) {
                        double w = 1.0/wordarrbpe.size();
                        for(String bpel:wordarrbpe){
                            if (Sub.sublistBPE.get(bpel).weight.containsKey(wordarrsw.get(0)) == false)
                                Sub.sublistBPE.get(bpel). weight.put(wordarrsw.get(0),0.0);
                            Sub.sublistBPE.get(bpel).weight.put(wordarrsw.get(0),Sub.sublistBPE.get(bpel).weight.get(wordarrsw.get(0))+w);
                        }
                    } else {
                        if (wordarrbpe.size() == wordarrsw.size()){
                            for(int p = 0;wordarrsw.size() > 0;p = p+1){
                                swsw = wordarrsw.get(0).replace("##","");
                                swbpe = wordarrbpe.get(0).replace("@@","").replace("@-@","-");


                                if (swsw.equals(swbpe)){
                                    add(wordarrbpe.get(0),wordarrsw.get(0),1.0);
                                    wordarrsw.remove(0);
                                    wordarrbpe.remove(0);
                                } else {
                                    break;
                                }

                            }
                            for(;wordarrsw.size()>0;){
                                swbpe= wordarrbpe.get(wordarrbpe.size()-1).replace("@@","").replace("@-@","-");
                                swsw = wordarrsw.get(wordarrsw.size()-1).replace("##","");
                                if (swsw.equals(swbpe)){
                                    add(wordarrbpe.get(wordarrbpe.size()-1),wordarrsw.get(wordarrbpe.size()-1), 1.0);
                                    wordarrsw.remove(wordarrbpe.size()-1);
                                    wordarrbpe.remove(wordarrbpe.size()-1);
                                } else {
                                    break;
                                }

                            }
                        }
                        double w = (1.0-1+wordarrsw.size()+0.0)/wordarrbpe.size();
                        for(String bpe:wordarrbpe){
                            for(String sw:wordarrsw) {
                                add(bpe, sw, w);
                            }
                        }
                    }
                }
            }
        }

        public static void soft(String s) {
            Sub sub = sublistBPE.get(s);
            Double s1 = 0.0;
        }

        static public sentence generatesentence(String data,String bpe,String sw) {
            String[]sarrdata = data.split(" ");
            String[]sarrbpe = bpe.split(" ");
            String[]sarrsw = sw.split(" ");
            sentence sentence = new sentence();
            sentence.word = new ArrayList<word>();
            sentence.bpeword = new ArrayList<bpeword>();
            sentence.swword = new ArrayList<bpeword>();
            for(String s:sarrdata){
                s = s.replace("&quot;","\"").replace("&apos;","\'");
                s=s.replace("&#93;","]");
                s=s.replace("&#91;","[");
                s=s.replace("&amp;","&");
                s=s.replace("&lt;","<").replace("&gt;",">").replace("&#124;","|");
                word word = new word();
                word.w = s;
                sentence.word.add(word);
            }
            for(String s:sarrbpe){
                s = s.replace("&quot;","\"").replace("&apos;","\'");
                s=s.replace("&#93;","]");
                s=s.replace("&#91;","[");
                s=s.replace("&amp;","&");
                s=s.replace("&lt;","<").replace("&gt;",">").replace("&#124;","|");

                bpeword word = new bpeword();
                word.w = s;
                sentence.bpeword.add(word);
            }
            for(String s:sarrsw){
                s = s.replace("&quot;","\"").replace("&apos;","\'");
                s=s.replace("&#93;","]");
                s=s.replace("&#91;","[");
                s=s.replace("&amp;","&");
                s=s.replace("&lt;","<").replace("&gt;",">").replace("&#124;","|");

                bpeword word = new bpeword();
                word.w = s;
                sentence.swword.add(word);
            }
            sentence.sentence = data;
            sentence.sentencebpeword=bpe;
            sentence.sentenceswword=sw;
            return sentence;
        }

    }
    public static void readDicBPE(String f1) {
        try{
            Scanner scanner = new Scanner(new FileInputStream(f1));
            Integer j=0;
            String l = null;
            while(scanner.hasNextLine()){
                l= scanner.nextLine();
                String[]ls = l.split(" ");
                l=ls[0].replace("&quot;","\"").replace("&apos;","\'").replace("&#93;","]").replace("&#91;","[").replace("&amp;","&").replace("&lt;","<").replace("&gt;",">").replace("&#124;","|").replace(" ","").replace("\n","");

                Sub sub = new Sub(l,j);
                Sub.sublistBPE.put(l,sub);
                Sub.idlist.put(j,l);
                j = 1+j;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }public static void readDicSW(String f1) {
        try{
            Scanner scanner = new Scanner(new FileInputStream(f1));
            Integer j=0;
            String l = null;
            while(scanner.hasNextLine() && (l= scanner.nextLine()) != null && !l.replace(" ","").replace("\n","").equals("")){
                l = l.replace(" ","").replace("\n","");
                Sub sub = new Sub(l,j);
                Sub.sublistSW.put(l,sub);
                j = 1+j;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public static void main(String []args) {
        String pathBpeDic = args[0];
        String pathSWDic = args[1];
        String pathData = args[2];
        String pathDataBPE = args[3];
        String pathDataSW = args[4];
        String pathNoUse = args[5];
        String pathSave = args[6];
        int fileCous = 0;
        if (args.length > 7){
            fileCous = Integer.parseInt(args[7]);
        }

        bpe.readDicBPE(pathBpeDic);
        bpe.readDicSW(pathSWDic);

        try {
            String sData = "";
            String sBPE = "";
            String sSW = "";
            BufferedWriter wSave = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathSave)));
            if(fileCous ==0){
                FileInputStream fData = new FileInputStream(pathData);
                FileInputStream fBPE = new FileInputStream(pathDataBPE);
                FileInputStream fSW = new FileInputStream(pathDataSW);
                byte[]byData=new byte[(int)new File(pathData).length()];
                fData.read(byData);
                sData = new String(byData);
                byte[]byBPE=new byte[(int)new File(pathDataBPE).length()];
                fBPE.read(byBPE);
                sBPE = new String(byBPE);
                byte[]bySW=new byte[(int)new File(pathDataSW).length()];
                fSW.read(bySW);
                sSW = new String(bySW);
                fData.close();
                fBPE.close();
                fSW.close();

                String [] arrData = sData.split("\n");
                String [] arrBPE = sBPE.split("\n");
                String [] arrSW = sSW.split("\n");
                String data,bpe,sw;
                for(int p=0;p<arrData.length;p=1+p){
                    data=arrData[p];bpe=arrBPE[p];sw=arrSW[p];
                    Sub.sentence(Sub.generatesentence(data,bpe,sw));
                    System.out.println(p);
                }
            } else {
                for(int l = 0 ; fileCous > l ; l = 1+l){
                    String pathData1 = pathData + "." + (l);
                    String pathDataBPE1 = pathDataBPE+"."+l;
                    String pathDataSW1 = pathDataSW+ "." + l;


                    FileInputStream fData = new FileInputStream(pathData1);
                    FileInputStream fBPE = new FileInputStream(pathDataBPE1);
                    FileInputStream fSW = new FileInputStream(pathDataSW1);
                    byte[]byData=new byte[(int)new File(pathData1).length()];
                    fData.read(byData);
                    sData = new String(byData);
                    byte[]byBPE=new byte[(int)new File(pathDataBPE1).length()];
                    fBPE.read(byBPE);
                    sBPE = new String(byBPE);
                    byte[]bySW=new byte[(int)new File(pathDataSW1).length()];
                    fSW.read(bySW);
                    sSW = new String(bySW);
                    fData.close();
                    fBPE.close();
                    fSW.close();

                    String [] arrData = sData.split("\n");
                    String [] arrBPE = sBPE.split("\n");
                    String [] arrSW = sSW.split("\n");
                    String data,bpe,sw;
                    for(int p=0;p<arrData.length;p=1+p){
                        data=arrData[p];bpe=arrBPE[p];sw=arrSW[p];
                        Sub.sentence(Sub.generatesentence(data,bpe,sw));
                        System.out.println(p);
                    }
                }
            }
            for(int p=0;p<Sub.idlist.size();p=p+1){
                wSave.write(Sub.idlist.get(p) + " ");
                for(String s:Sub.sublistBPE.get(Sub.idlist.get(p)).weight.keySet()){
                    wSave.write(s + " " + Sub.sublistBPE.get(Sub.idlist.get(p)).weight.get(s)+ " ");
                }
                wSave.write("\n");
            }
            wSave.flush();
            wSave.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
