package ftpclient;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class ftpclient extends JFrame implements ActionListener
{
	//ͼ�ν������
  JTextField address,user_name,net_route,local_route,list_name,file_name,inlist_name;
  JPasswordField user_password;
  JButton connect,disconnect,download_Button,upload_Button,mkdir_Button,delete_Button,back_Button,into_Button;
  JLabel Condition,Route;//ConditionΪ״̬��ʾ����RouteΪ��ǰ�ڴ�վ��ĵ�ǰĿ¼
  JPanel message_Panel;
  JTable table;//��ʾĿ¼����Ϣ�б�
  Object data[][],name[]={"�ļ���","�ļ����ļ���","�ļ���С�����ļ���","����ʱ��"};
  
  //ftp���Ӳ���
  String s=null;
  Socket mysocket;
  DataInputStream in=null;
  DataOutputStream out=null;
  boolean linked=false;
  
  ftpclient()
  { 
    super("���������ʵ�����ҵ����FTP����");
    //�������������
    Container con=getContentPane();
    Box box1,box2,box3,box4,box5,box6,box7,box;
    
    box1=Box.createHorizontalBox();
    box1.add(Box.createHorizontalStrut(5));
    box1.add(new JLabel("FTP��������ַ��"));
    address=new JTextField("127.0.0.1",50);
    box1.add(address);
    
    box2=Box.createHorizontalBox();
    box2.add(Box.createHorizontalStrut(5));
    box2.add(new JLabel("�û�����"));
    user_name=new JTextField("user",20);
    box2.add(user_name);
    box2.add(new JLabel("���룺"));
    user_password=new JPasswordField("123456",15);
    box2.add(user_password);
    connect=new JButton("����");
    disconnect=new JButton("�Ͽ�");
    box2.add(connect);box2.add(Box.createHorizontalStrut(10));box2.add(disconnect);
    
    box3=Box.createHorizontalBox();
    box3.add(Box.createHorizontalStrut(5));
    box3.add(new JLabel("��������·����"));
    Route=new JLabel();
    box3.add(Route);
    net_route=new JTextField();    
    box3.add(net_route);
    
    box4=Box.createHorizontalBox();
    box4.add(Box.createHorizontalStrut(5));
    local_route=new JTextField();
    box4.add(new JLabel("����·����"));
    box4.add(local_route);
    
    box5=Box.createHorizontalBox();
    download_Button=new JButton("����");upload_Button=new JButton("�ϴ�");
    box5.add(download_Button);box5.add(Box.createHorizontalStrut(100));box5.add(upload_Button);
    
    box6=Box.createHorizontalBox();
    box6.add(Box.createHorizontalStrut(5));
    mkdir_Button=new JButton("����Ŀ¼");delete_Button=new JButton("ɾ��");    
    list_name=new JTextField(10);file_name=new JTextField(15);
    box6.add(new JLabel("Ŀ¼����"));box6.add(list_name);box6.add(mkdir_Button);
    box6.add(Box.createHorizontalStrut(10));
    box6.add(new JLabel("�ļ�����"));box6.add(file_name);box6.add(delete_Button);
    
    box7=Box.createHorizontalBox();  
    box7.add(Box.createHorizontalStrut(5)); 
    box7.add(new JLabel("����Ŀ¼��")); 
    inlist_name=new JTextField();
    box7.add(inlist_name);
    into_Button=new JButton("ȷ��");
    box7.add(into_Button);   
    back_Button=new JButton("������һ��");
    box7.add(back_Button);     
    
    box=Box.createVerticalBox();  
    box.add(Box.createVerticalStrut(10));box.add(box1);
    box.add(Box.createVerticalStrut(10));box.add(box2);
    box.add(Box.createVerticalStrut(10));box.add(box3);
    box.add(Box.createVerticalStrut(10));box.add(box4);
    box.add(Box.createVerticalStrut(10));box.add(box5);
    box.add(Box.createVerticalStrut(10));box.add(box6);
    box.add(Box.createVerticalStrut(10));box.add(box7);   
    
    message_Panel=new JPanel();
    message_Panel.setLayout(new BorderLayout());
    message_Panel.setBackground(Color.cyan);
    message_Panel.add(new JLabel("��ǰĿ¼�µ��ļ����ļ�����Ϣ"),BorderLayout.NORTH);
    data=new Object[500][4];//�����ʾ500����Ϣ
    table=new JTable(data,name);
    table.setEnabled(false);
    table.setRowHeight(20);
    table.setForeground(Color.blue);
    message_Panel.add(new JScrollPane(table),BorderLayout.CENTER);
    con.add(message_Panel,BorderLayout.CENTER);
    
    JPanel panel1=new JPanel();
    panel1.add(new JLabel("��ǰ״̬��"));
    Condition=new JLabel("δ����");
    Condition.setForeground(Color.red);
    panel1.add(Condition);
    con.add(box,BorderLayout.NORTH);
    con.add(panel1,BorderLayout.SOUTH);
    con.setBackground(Color.orange);
    validate();
    setBounds(300,100,600,650);
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    //�����¼�������
    download_Button.addActionListener(this);//����
    upload_Button.addActionListener(this);
    mkdir_Button.addActionListener(this);
    delete_Button.addActionListener(this);
    back_Button.addActionListener(this);
    into_Button.addActionListener(this); 
    connect.addActionListener(this);
    disconnect.addActionListener(this);
  }
  public boolean showing()
  {
     try
     {
        int n=in.readInt();
        if(n==0)
        {
          String mess=in.readUTF();
          Condition.setText(mess);
          return false;
        }
        int i=0,j;         
        for(;i<n;i++)
        {
         for(j=0;j<4;j++)
          {            
           data[i][j]=in.readUTF();          
          }
          if(data[i][1].equals("�ļ�"))data[i][2]=data[i][2]+" Byte";
          data[i][3]=new Date(Long.parseLong(data[i][3].toString())).toString();            
        }  
       for(;i<500;i++)
        {
         for(j=0;j<4;j++)
          {            
           data[i][j]="";          
          }             
        }          
     }
     catch(IOException e1){
    	 Condition.setText("������������Ժ�����!");
    	 }
     repaint();
     return true;
  }
  public void actionPerformed(ActionEvent e)//�����¼�����
  {
    if(e.getSource()==connect)//���ӵ�ָ��������
    {
      try
      {
        mysocket=new Socket(address.getText().trim(),21);
        in=new DataInputStream(mysocket.getInputStream());
        out=new DataOutputStream(mysocket.getOutputStream());
        out.writeUTF(user_name.getText().trim());        
        out.writeUTF(new String(user_password.getPassword()).trim());
        String state;
        state=in.readUTF();
        Condition.setText(state+"��"+address.getText());
        Route.setText("");
        if(state.equals("�û�user��¼�ɹ�"))
        {
          linked=true;
          try
          {
          out.writeUTF("dir");
          }
          catch(IOException e1){Condition.setText("������������Ժ�����!");}
          out.writeUTF(".");
          showing();      
        }        
      }
      catch(IOException e1)
      {
        Condition.setText("�޷����ӵ�����������������!");
      }
      repaint();
    }
    else if(e.getSource()==disconnect)                                         //�Ͽ�����
    {
       if(out!=null)
       {
         try
         {
          out.writeUTF("byebye");
         }
         catch(IOException e1){Condition.setText("�ѶϿ�����!");}
         try
         {
          mysocket.close();
          Route.setText("");
          Condition.setText("�ѶϿ�����!");
         }
         catch(IOException e1){}
       }
    }
    else if(e.getSource()==download_Button)                                           //����ָ���ļ�
    {     
     try
      {
        out.writeUTF("load");
        String tem=Route.getText()+net_route.getText();
        out.writeUTF(tem);
      }
     catch(IOException e1){Condition.setText("������������Ժ�����!");}   
     try
     { 
        if(in.readUTF().equals("ok"))
        {
         File loadfile=new File(local_route.getText());
         if(!loadfile.createNewFile()){Condition.setText("�ļ��Ѵ���");out.writeUTF("����ȡ��");}
         else
         {     
           out.writeUTF("��ʼ����......");    
           Condition.setText("���ڽ���......");        
           long file_length=in.readLong(),read_length=0l;
           FileOutputStream newfile=new FileOutputStream(loadfile);
           byte b[]=new byte[1];
           int n=0;       
           while((n=in.read(b,0,1))>0)
           {              
            newfile.write(b);
            read_length+=1;
            if(read_length>=file_length)break;
           }
          newfile.close();
          Condition.setText("�������!");          
         }
        }
        else
        {Condition.setText("ָ������Դ�ļ�������!");
        out.writeUTF("����ȡ��");
        }
     }
     catch(IOException e1){Condition.setText("?������������Ժ�����!");}   
   }
    else if(e.getSource()==upload_Button)                                             //�ϴ�ָ���ļ�
    {      
      File uploadfile=new File(local_route.getText());
      if(uploadfile.isFile()&&!(net_route.getText().trim().equals("")))
      {
       String messa;
       try
        {
         out.writeUTF("build"); 
         out.writeUTF(Route.getText()+net_route.getText());
         if((messa=in.readUTF()).equals("��ʼ�ϴ�......"))
         {
          out.writeLong(uploadfile.length());
          Condition.setText(messa);
          FileInputStream upload=new FileInputStream(uploadfile);
          byte b[]=new byte[1];
          int n=0;
          while((n=upload.read(b))!=-1)
          {
           out.write(b);
          }
          Condition.setText("�ļ��ϴ��ɹ�!");
          }
          else{Condition.setText(messa);}
         }
       catch(IOException e1){Condition.setText("������������Ժ�����!");}  
      }
      else if(!uploadfile.isFile())
      {
       Condition.setText("�ļ�������!");
      }
      else
      {
       Condition.setText("��Ŀ���ļ���!");
      }
    }
    else if(e.getSource()==mkdir_Button)                                       //������Ŀ¼
    {
      try
      {
        out.writeUTF("mkdir");
        out.writeUTF(Route.getText()+list_name.getText());
        String messag=in.readUTF();
        Condition.setText(messag);
        if(messag.equals("��Ŀ¼�ѳɹ�����!"))
        {
         out.writeUTF(Route.getText());
         showing();
        }
      }
      catch(IOException e1){Condition.setText("������������Ժ�����!");} 
    }
    else if(e.getSource()==delete_Button)                                              //ɾ��ָ���ļ�
    {
      try
      {
        out.writeUTF("delete");
        out.writeUTF(Route.getText()+file_name.getText());
        String messa=in.readUTF();
        Condition.setText(messa);
        if(!(messa.equals("�ļ�������")))
        {
         out.writeUTF(Route.getText());
         showing(); 
        }
      }
      catch(IOException e1){Condition.setText("������������Ժ�����!");}       
    }
    else if(e.getSource()==into_Button)                                                  //������Ŀ¼
    {
     try
     {
      out.writeUTF("cd");
      if(!(Route.getText().equals("."))&&!(Route.getText().equals("")))out.writeUTF(Route.getText()+"\\"+inlist_name.getText());
      else out.writeUTF(inlist_name.getText());
      if(showing())
      {
       if(!(inlist_name.getText().equals(".")))Route.setText(Route.getText()+"\\"+inlist_name.getText());
       Condition.setText("��ӭ�û�user������վ!");
      }
     }
     catch(IOException e1){Condition.setText("������������Ժ�����!");}
    }
    else if(e.getSource()==back_Button)                                               //������һ��
    {
     try
     {
      out.writeUTF("return");
      String tem="\\"+Route.getText();
      if(!(tem.equals("")))
      {
      tem=tem.substring(0,tem.lastIndexOf("\\"));
      Route.setText(tem);
      }
      else tem=".";
      out.writeUTF(tem);
      if(showing())
      {
       if(!(Route.getText().equals("")))
       Route.setText(Route.getText()+"\\"+inlist_name.getText());
       else Route.setText(inlist_name.getText());
       Condition.setText("��ӭ�û�user������վ!");
      }
     }
     catch(IOException e1){Condition.setText("������������Ժ�����!");} 
    }
  }
  public static void main(String args[])
  {
    new ftpclient();
  }
}
