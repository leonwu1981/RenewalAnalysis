package com.sinosoft.lis.pubfun;
/**
 * 数组排序辅助类TwoDimArrayComparator
 * author:hm 2008-3-20
 */
import java.util.Arrays;
import java.util.Comparator;
public class TwoDimArrayComparator implements Comparator{

 private int keyColumn = 0;
    private int sortOrder = 1;
    
    public TwoDimArrayComparator () {}
    public TwoDimArrayComparator (int keyColumn) {
        this.keyColumn = keyColumn;    
    }
    public TwoDimArrayComparator (int keyColumn,int sortOrder) {
        this.keyColumn = keyColumn;    
        this.sortOrder = sortOrder;
    }
    
    public int compare(Object a, Object b) {
        if (a instanceof String[]) {
            return sortOrder * ((String[])a)[keyColumn].compareTo(((String[])b)[keyColumn]);
        } else if (a instanceof int[]){
            return sortOrder * (((int[])a)[keyColumn] - ((int[])b)[keyColumn]);        
        } else {        
            return 0;       
        }
    }  
    
    //  打印二维数组方法
    public static void printArray(String[][] arr) {
           for (int i= 0; i< arr.length ; i++) {
               for (int j = 0; j < arr[i].length; j++) {
                   System.out.print(arr[i][j] +" ");
               }
               System.out.println();
           }
       } 
    /**
	 * 主函数，测试用
	 * 解决理算类多责任共用免赔额
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
//		String [][] s1 ={{"0.8","610201","000"},
//			             {"0.5","610205","000"},
//			             {"0.7","610204","001"},
//			             {"0.8","610208","100"},
//			             {"0.85","610202","002"}
//			       };
		
		String [][] s1= new String[3][4];
		s1[0][0]="0.7";
		s1[0][1]="610205";
		s1[0][2]="001";
		s1[0][3]="y";
		s1[1][0]="0.5";
		s1[1][1]="610204";
		s1[1][2]="101";
		s1[1][3]="n";
		s1[2][0]="1";
		s1[2][1]="610206";
		s1[2][2]="102";
		s1[2][3]="y";
		
		TwoDimArrayComparator.printArray(s1);
		System.out.println("=======================");
		//  Arrays.sort(s1, new TwoDimArrayComparator());  //下标从0开始,默认为0,按第一列排序
		  Arrays.sort(s1, new TwoDimArrayComparator(1));  //按第二列排序
		  TwoDimArrayComparator.printArray(s1);

}
}
