package com.newlife.Connect_multiple.test;

import java.util.ArrayList;

public class Test {
    private static int[] standardScore = {1, -1, -1, 1, -1, 1};
    // chu kỳ sai mẫu 1
//    private static String[] wrongScore = {"1", "No Event", "-1", "1", "-1", "1", "1", "-1", "-1", "1", "-1", "1"};
//    private static String[] wrongScore = {"1", "-1", "-1", "No Event", "-1", "1", "1", "-1", "-1", "1", "-1", "1"};
    private static String[] wrongScore = {"1", "-1", "-1", "1", "-1", "No Event", "No Event", "-1", "-1", "1", "-1", "1"};
    private static ArrayList<String> hashStandard = new ArrayList<>();
    private static String[] hashStandard_x = new String[10];
    public static void main(String[] args) {
        ArrayList<Integer> index_wrong = new ArrayList<>();
        // create hash standard
        createHashStandard();
        for(int i = 0; i <= wrongScore.length - 6;) {
            String[] tmp = createHash(i, i + 5).split("_");
            // khoảng x -> x+5 có sai số
            if(!hashStandard.contains(tmp[0])) {
                System.out.println("Vị trí " + (i+1));
                System.out.println("Hash_x_standard " + hashStandard_x[(i+1) % 6]);
                System.out.println("Hash_x " + tmp[1]);
                System.out.println("==============================================================");
                if(!tmp[1].equals(hashStandard_x[i+1])) {
                    System.out.println("Vị trí sai " + (i+1));
                    index_wrong.add(i+1); // sai ở vị trí i
                    System.out.println("==============================================================");
                }
                i++;
            }
            else {
                i += 5;
            }
        }
        System.out.println("Vị trí sai: " + index_wrong.toString());
    }

    private static String createHash(int start, int end) {
        String hash_x_y = "";
        String hash_x = "";
        // tính hash từ start -> end(start + 6)
        for(int i = start; i <= end; i++) {
            hash_x_y += wrongScore[i];
        }
        // tính hash tại vị trí start
        for(int i = 0; i <= start; i++) {
            hash_x += wrongScore[i];
        }
        return hash_x_y + "_" + hash_x;
    }

    private static void createHashStandard() {
        for(int i = 1; i<=6; i++) {
            String hash = "";
            String hash_x = "";
            for(int j = i-1; j<=i+4; j++) {
                int tmp = j > standardScore.length - 1 ? j % standardScore.length : j;
//                System.out.println("Tmp " + tmp + " Score " + standardScore[tmp]);
                hash += standardScore[tmp];
            }
            for(int j = 0; j<i; j++) {
                hash_x += standardScore[j];
            }
            hashStandard.add(hash);
            hashStandard_x[i] = hash_x;
        }
    }
}
