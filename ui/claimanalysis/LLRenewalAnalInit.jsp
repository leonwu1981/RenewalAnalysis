<%@include file="../common/jsp/UsrCheck.jsp"%>
<SCRIPT src="../common/javascript/Common.js"></SCRIPT>

<script language="JavaScript">

function initForm() {
  try {
    initCustomGrid();
    initCheckedGrid();
    initReqGrid();
    //
    divQueryType1.style.display="";
    divQueryType2.style.display="none";
    if(radioNum=="2"){
    	divQueryType1.style.display="none";
        divQueryType2.style.display="";
        refreshReq();
    }
  }
  catch(re) {
    alert("LLRenewalAnalInit.jsp-->InitForm�����з����쳣:��ʼ���������!");
  }
}

function initCustomGrid() {
	  var iArray = new Array();

	  try {
	    iArray[0]=new Array();
	    iArray[0][0]="���";                 //����
	    iArray[0][1]="30px";                 //����
	    iArray[0][3]=0;                 //����

	    iArray[1]=new Array();
	    iArray[1][0]="��λ����";               //����
	    iArray[1][1]="115px";                //�п�
	    iArray[1][2]=100;                        //�����ֵ
	    iArray[1][3]=0;                          //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[2]=new Array();
	    iArray[2][0]="��ϵ��";
	    iArray[2][1]="35px";
	    iArray[2][2]=200;
	    iArray[2][3]=0;

	    iArray[3]=new Array();
	    iArray[3][0]="��ϵ�绰";             //����
	    iArray[3][1]="0px";                //�п�
	    iArray[3][2]=200;                   //�����ֵ
	    iArray[3][3]=3;                      //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[4]=new Array();
	    iArray[4][0]="��������";             //����
	    iArray[4][1]="40px";                //�п�
	    iArray[4][2]=200;                   //�����ֵ
	    iArray[4][3]=0;                      //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[5]=new Array();
	    iArray[5][0]="��λ����";             //����
	    iArray[5][1]="0px";                //�п�
	    iArray[5][2]=200;                   //�����ֵ
	    iArray[5][3]=3;                      //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[6]=new Array();
	    iArray[6][0]="Ͷ��Ա������";             //����
	    iArray[6][1]="55px";                //�п�
	    iArray[6][2]=200;                   //�����ֵ
	    iArray[6][3]=0;  

	    iArray[7]=new Array();
	    iArray[7][0]="�ͻ���";             //����
	    iArray[7][1]="0px";                //�п�
	    iArray[7][2]=200;                   //�����ֵ
	    iArray[7][3]=0; 
	    
	    iArray[8]=new Array();
	    iArray[8][0]="��Ч����";             //����
	    iArray[8][1]="50px";                //�п�
	    iArray[8][2]=200;                   //�����ֵ
	    iArray[8][3]=0; 
	    
	    iArray[9]=new Array();
	    iArray[9][0]="������";             //����
	    iArray[9][1]="0px";                //�п�
	    iArray[9][2]=200;                   //�����ֵ
	    iArray[9][3]=3; 
	    
	    iArray[10]=new Array();
	    iArray[10][0]="�ܱ���";             //����
	    iArray[10][1]="40px";                //�п�
	    iArray[10][2]=200;                   //�����ֵ
	    iArray[10][3]=0; 
	    
	    iArray[11]=new Array();
	    iArray[11][0]="�ܱ���";             //����
	    iArray[11][1]="40px";                //�п�
	    iArray[11][2]=200;                   //�����ֵ
	    iArray[11][3]=0; 
	    
	    iArray[12]=new Array();
	    iArray[12][0]="��������";             //����
	    iArray[12][1]="40px";                //�п�
	    iArray[12][2]=200;                   //�����ֵ
	    iArray[12][3]=0; 
	    
	    iArray[13]=new Array();
	    iArray[13][0]="���屣����";             //����
	    iArray[13][1]="50px";                //�п�
	    iArray[13][2]=200;                   //�����ֵ
	    iArray[13][3]=1; 
	    
	    
	    iArray[14]=new Array();
	    iArray[14][0]="Ͷ������";             //����
	    iArray[14][1]="0px";                //�п�
	    iArray[14][2]=200;                   //�����ֵ
	    iArray[14][3]=3; 
	    
	    CustomGrid = new MulLineEnter("fm", "CustomGrid");
	    //����Grid����
	    CustomGrid.mulLineCount = 0;
	    CustomGrid.displayTitle = 1;
	    CustomGrid.locked = 1;
	    //CustomGrid.canSel = 1;
	    CustomGrid.canChk = 1;
	    
	    CustomGrid.hiddenSubtraction = 1;
	    CustomGrid.hiddenPlus = 1;
	    CustomGrid.loadMulLine(iArray);
	  }
	  catch(ex) {
	    alert(ex);
	  }
	}

function initCheckedGrid() {
	  var iArray = new Array();

	  try {
	    iArray[0]=new Array();
	    iArray[0][0]="���";                 //����
	    iArray[0][1]="30px";                 //����
	    iArray[0][3]=0;                 //����

	    iArray[1]=new Array();
	    iArray[1][0]="��λ����";               //����
	    iArray[1][1]="115px";                //�п�
	    iArray[1][2]=100;                        //�����ֵ
	    iArray[1][3]=0;                          //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[2]=new Array();
	    iArray[2][0]="��ϵ��";
	    iArray[2][1]="35px";
	    iArray[2][2]=200;
	    iArray[2][3]=0;

	    iArray[3]=new Array();
	    iArray[3][0]="��ϵ�绰";             //����
	    iArray[3][1]="0px";                //�п�
	    iArray[3][2]=200;                   //�����ֵ
	    iArray[3][3]=3;                      //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[4]=new Array();
	    iArray[4][0]="��������";             //����
	    iArray[4][1]="40px";                //�п�
	    iArray[4][2]=200;                   //�����ֵ
	    iArray[4][3]=0;                      //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[5]=new Array();
	    iArray[5][0]="��λ����";             //����
	    iArray[5][1]="0px";                //�п�
	    iArray[5][2]=200;                   //�����ֵ
	    iArray[5][3]=3;                      //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[6]=new Array();
	    iArray[6][0]="Ͷ��Ա������";             //����
	    iArray[6][1]="55px";                //�п�
	    iArray[6][2]=200;                   //�����ֵ
	    iArray[6][3]=0;  

	    iArray[7]=new Array();
	    iArray[7][0]="�ͻ���";             //����
	    iArray[7][1]="0px";                //�п�
	    iArray[7][2]=200;                   //�����ֵ
	    iArray[7][3]=0; 
	    
	    iArray[8]=new Array();
	    iArray[8][0]="��Ч����";             //����
	    iArray[8][1]="50px";                //�п�
	    iArray[8][2]=200;                   //�����ֵ
	    iArray[8][3]=0; 
	    
	    iArray[9]=new Array();
	    iArray[9][0]="������";             //����
	    iArray[9][1]="0px";                //�п�
	    iArray[9][2]=200;                   //�����ֵ
	    iArray[9][3]=3; 
	    
	    iArray[10]=new Array();
	    iArray[10][0]="�ܱ���";             //����
	    iArray[10][1]="40px";                //�п�
	    iArray[10][2]=200;                   //�����ֵ
	    iArray[10][3]=0; 
	    
	    iArray[11]=new Array();
	    iArray[11][0]="�ܱ���";             //����
	    iArray[11][1]="40px";                //�п�
	    iArray[11][2]=200;                   //�����ֵ
	    iArray[11][3]=0; 
	    
	    iArray[12]=new Array();
	    iArray[12][0]="��������";             //����
	    iArray[12][1]="40px";                //�п�
	    iArray[12][2]=200;                   //�����ֵ
	    iArray[12][3]=0; 
	    
	    iArray[13]=new Array();
	    iArray[13][0]="���屣����";             //����
	    iArray[13][1]="50px";                //�п�
	    iArray[13][2]=200;                   //�����ֵ
	    iArray[13][3]=1; 
	    
	    
	    iArray[14]=new Array();
	    iArray[14][0]="Ͷ������";             //����
	    iArray[14][1]="0px";                //�п�
	    iArray[14][2]=200;                   //�����ֵ
	    iArray[14][3]=3; 
	    
	    CheckedGrid = new MulLineEnter("fm", "CheckedGrid");
	    //����Grid����
	    CheckedGrid.mulLineCount = 0;
	    CheckedGrid.displayTitle = 1;
	    CheckedGrid.locked = 1;
	    //CheckedGrid.canSel = 1;
	    CheckedGrid.canChk = 1;
	    
	    CheckedGrid.hiddenSubtraction = 1;
	    CheckedGrid.hiddenPlus = 1;
	    CheckedGrid.loadMulLine(iArray);
	  }
	  catch(ex) {
	    alert(ex);
	  }
	}

function initReqGrid() {
	var iArray = new Array();
	try {
		iArray[0]=new Array();
	    iArray[0][0]="���";                 //����
	    iArray[0][1]="30px";                 //����
	    iArray[0][3]=0;                 //����
		
	    iArray[1]=new Array();
	    iArray[1][0]="��������";                 //����
	    iArray[1][1]="100px";                 //����
	    iArray[1][2]=200;                        //�����ֵ
	    iArray[1][3]=0;                 //����

	    iArray[2]=new Array();
	    iArray[2][0]="������";                 //����
	    iArray[2][1]="60px";                 //����
	    iArray[2][2]=200;                        //�����ֵ
	    iArray[2][3]=0;                 //����

	    iArray[3]=new Array();
	    iArray[3][0]="����״̬";               //����
	    iArray[3][1]="60px";                //�п�
	    iArray[3][2]=100;                        //�����ֵ
	    iArray[3][3]=0;                          //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[4]=new Array();
	    iArray[4][0]="��ѯ����";               //����
	    iArray[4][1]="200px";                //�п�
	    iArray[4][2]=280;                        //�����ֵ
	    iArray[4][3]=0;                          //�Ƿ���������,1��ʾ����0��ʾ������

	    //iArray[5]=new Array();
	    //iArray[5][0]="��ʼ����";               //����
	    //iArray[5][1]="0px";                //�п�
	    //iArray[5][2]=100;                        //�����ֵ
	    //iArray[5][3]=0;                          //�Ƿ���������,1��ʾ����0��ʾ������

	    //iArray[6]=new Array();
	    //iArray[6][0]="��������";               //����
	    //iArray[6][1]="0px";                //�п�
	    //iArray[6][2]=100;                        //�����ֵ
	    //iArray[6][3]=0;                          //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[5]=new Array();
	    iArray[5][0]="����˵��";               //����
	    iArray[5][1]="180px";                //�п�
	    iArray[5][2]=100;                        //�����ֵ
	    iArray[5][3]=0;                          //�Ƿ���������,1��ʾ����0��ʾ������

	    ReqGrid = new MulLineEnter("fm", "ReqGrid");
	    //����Grid����
	    ReqGrid.mulLineCount = 1;
	    ReqGrid.displayTitle = 1;
	    ReqGrid.locked = 1;
	    ReqGrid.canSel = 1;
	    ReqGrid.canChk = 0;
	    
	    ReqGrid.hiddenSubtraction = 1;
	    ReqGrid.hiddenPlus = 1;
	    ReqGrid.loadMulLine(iArray);
	} catch(ex) {
		alert(ex);
	}
}
</script>