package com.sinosoft.utility;

import   java.util.*;   
 /**   
   *   @author  hanming 2009-10-10 Vector 中 String[] 排序   
   *   @version   1.0   
   */   
   
   
 public   class   SortListTable   implements   Comparable{   
     String[] arr;
     int   i=0;   
     int   ascendOrDescend=1;//1:ascend     0:Descend   
     String   s1="";   
     String   s2="";   
     
     //construct   function   
     public   SortListTable()
     {   
         super();   
     }  
     
     //construct   function   
     public   SortListTable(String[]  v,int   i)
     {   
         this.arr=v;   
         this.i=i;   
     }   
     
     //construct   function   
     public   SortListTable(String[]   v,int   i,int   ascendOrDescend)
     {   
   	  this.arr=v;   
   	  this.i=i;   
   	  this.ascendOrDescend=ascendOrDescend;   
     }   

     //重写此方法
     public   int   compareTo(Object   o)
     {   
    	 SortListTable   sv=(SortListTable)o;   
         s1=(sv.arr[i]).toString();   
         s2=(this.arr[i]).toString();   
         int   mm=s1.compareTo(s2.toString());   
         if(this.ascendOrDescend==1)   
             mm=-mm;   
         return   mm;   
     }   

     public   String[]   getStringArr()
     {   
         return   this.arr;   
     }  
   
   
     public   ArrayList   returnLBysortDVector(Vector   dv,int   m,int   n)
     {   
   	  ArrayList   list   =new   ArrayList();   
   	  Vector   v=new   Vector();   
   	  if(dv.size()>0){   
         //System.out.println("=======dv.size()==========="+dv.size());   
   		  for(int   j=0;j<dv.size();j++)
   		  {    
   			  SortListTable   sev=new   SortListTable((String[])(dv.elementAt(j)),m,n);   
   			  list.add(sev);    
   		  }   
   		  Collections.sort(list);   
   	  }   
   	  return   list;   
     }   
     
     public   Vector   returnVBysortElementInVector(Vector   dv,int   m)
     {   
         ArrayList   list   =new   ArrayList();   
         Vector   v=new   Vector();   
         if(dv.size()>0)
         {   
           //  System.out.println("=======dv.size()==========="+dv.size());   
             for(int   j=0;j<dv.size();j++)
             {   
            	 SortListTable   sev=new   SortListTable((String[])(dv.elementAt(j)),m);   
                 list.add(sev);   
             }   
             Collections.sort(list);   
             for(int   i=0;i<list.size();i++)
             {   
                 v.add(((SortListTable)list.get(i)).getStringArr());   
             }   
         }   
         return   v;   
     }  
     
     public   Vector   returnVBysortElementInVector(Vector   dv,int   m,int   n)
     {   
         ArrayList   list   =new   ArrayList();   
         Vector   v=new   Vector();   
         if(dv.size()>0)
         {   
           //  System.out.println("=======dv.size()==========="+dv.size());   
             for(int   j=0;j<dv.size();j++)
             {   
            	 SortListTable   sev=new   SortListTable((String[])(dv.elementAt(j)),m,n);   
                 list.add(sev);   
             }   
             Collections.sort(list);   
             for(int   i=0;i<list.size();i++)
             {   
                 v.add(((SortListTable)list.get(i)).getStringArr());   
             }   
         }   
         return   v;   
     }   
   
   public   static   void   main(String[]   args){   
       String[]   s1=new   String[3]; 
       s1[0] = "84000";
       s1[1] = "bbb";
       s1[2] = "ccc";
       String[]   s2=new   String[3]; 
       s2[0] = "8250000";
       s2[1] = "aaa";
       s2[2] = "ccc";
       String[]   s3=new   String[3]; 
       s3[0] = "828000000";
       s3[1] = "ddd";
       s3[2] = "ccc"; 
       
       Vector   v=new   Vector();   
       v.add(s1);   
       v.add(s2); 
       v.add(s3);
       System.out.println("===============Test   begin=========");   
       System.out.println("");   
       SortListTable   tSortListTable=new   SortListTable();  
       Vector   vvv=new   Vector();   
     //  vvv=tSortListTable.returnVBysortElementInVector(vs,0,0);  //2参数:按第0个排序 3参数：1升序0降序
    //   vvv=tSortListTable.returnVBysortElementInVector(v,0,1);  //2参数:按第0个排序
       vvv=tSortListTable.returnVBysortElementInVector(v,0);  //2参数:按第0个排序,默认升序
       for(int   j=0;j<vvv.size();j++){   
           System.out.println("======"+j+"=========");   
           String[]   arr=(String[])(vvv.elementAt(j));   
           for(int i=0;i<arr.length;i++) 
           {
           	   System.out.print(arr[i] + " ");
           }
           System.out.println();
          
       } 
       System.out.println("");   
       
       System.out.println("===============Test   end========="); 
       
   }   
 }   
   
