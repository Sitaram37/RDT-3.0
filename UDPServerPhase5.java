
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class UDPServer {

    private static final boolean DEBUG = true;
    private static boolean cond=true;
    
    private static byte[] create_Packet(int seq1,byte[]chkCalSum1)
	{
		byte[] packet= new byte[3];
		 packet[0]= (byte)(seq1 & 0xF);
			System.arraycopy(chkCalSum1, 0, packet, 1, chkCalSum1.length);
		return packet;
	}
	
    private static byte[] chkSum(byte[] pck)
	{ 
    	//System.out.println(" sender Checksum Value: "+ strData);
		byte[]ChkSum=new byte[2];
    	int a=0;
		long inData=0x0000;
		long dataSum=0x0000;
		//Random randNum= new Random();
		//int x= randNum.nextInt(pck.length);
	    	while(a<pck.length)	
	    	{
	    	inData=(((pck[a]<<8)& 0xFF00)|((pck[++a])& 0xFF));
	    	dataSum+=inData;
	    		if((dataSum & 0xFFFF0000)>0)//Checking for overflow/carry bit;
	    		{
	    		dataSum=dataSum & 0xFFFF;//removing the carry bit;
	    		dataSum=dataSum+0x1; //adding the carry bit;
	    		}
	    	a++;
	    	}
	    	ChkSum[0]=(byte)(dataSum>>8);
	    	ChkSum[1]=(byte)(dataSum & 0xFF);
	    	System.out.println("Receiver CheckSum: "+dataSum);
	    	return ChkSum;
	} 

    public static void main(String[] args) {
        if (DEBUG) {
            System.out.println("Starting the server...");
        }
        try {
            DatagramSocket serverSocket;
            serverSocket = new DatagramSocket(1556);
            byte[] receiveData = new byte[1027];
            byte[] sendData = new byte[1];
            int seq =0x0;
            long ChkSum=0x0;
            byte[] chkSumCal=new byte[2];
            int expSeq=0;
            byte[] strReceivedata = new byte[1024];
            Scanner inOption = new Scanner(System.in);
            int o=0;
            int base=0;
            int wnds=4;
            int ack=0;
            Random randNum= new Random();
            
            File dir = new File ("C:\\Users\\Kushal\\workspace\\Phase5\\kush.jpeg");
            FileOutputStream imageFile = new FileOutputStream(dir,true);
            DatagramPacket Packet;
            
            byte[] strAck=new byte[3];
            byte[] recLen=new byte[2];
            byte[] chkSum=new byte[2];
            byte[] ackSeq= new byte[1];
            
            Packet = new DatagramPacket(recLen, recLen.length);// Receiving Data Information;
            serverSocket.receive(Packet);
            //String recLength1= new String(recLen);
            
            InetAddress IpAddress=Packet.getAddress();
            int portSend=Packet.getPort();
            
            Packet = new DatagramPacket(sendData, sendData.length, IpAddress, portSend);
            serverSocket.send(Packet);
            
            long j= ((recLen[0]<<8)& 0XFF00)|(recLen[1]&0xFF);//File length.
            System.out.println(j);
            int i=0;
            
            receive:while (i<=j) {
            		receiveData= new byte[1027];
            		String recData= new String();
                	Packet = new DatagramPacket(receiveData, receiveData.length);
                   
                	System.out.println("Server is ready to accept data...");
                	
                	serverSocket.receive(Packet);
                	System.out.println("Packet received..." + i);
                    //System.out.println(new String(receiveData));
                    
                	long chkVal=((receiveData[1]<<8)& 0xFF00)|(receiveData[2]&0xFF);//Extracting the sender checksum
                    System.out.println("sender Checksum "+chkVal);
                    i++;
                    seq=(int)receiveData[0];
                    System.out.println("sequence number "+seq);
                    
                    int r=randNum.nextInt(100);//Generating the error ;
                   
                    System.arraycopy(receiveData,3, strReceivedata, 0, strReceivedata.length);
                    IpAddress=Packet.getAddress();
                    portSend=Packet.getPort();
                    chkSumCal=chkSum(strReceivedata);
                    ChkSum=(((chkSumCal[0]<<8)& 0xFF00)|(chkSumCal[1]&0xFF));
                    //System.out.println("Please enter the error percentage ");
    				//o= inOption.nextInt();
                    //if(r<40){ChkSum=randNum.nextInt(9999);}
                    while((chkVal|ChkSum)!=0xFFFF)//checking the integrity of the data.
                    {
                    	j++;
                    	ackSeq[0]=(byte)((seq)&0xFF);
                    	System.out.println("The recieived packet is currupted ,sending negative acknowledgement.....");
                    	Packet = new DatagramPacket(sendData, sendData.length, IpAddress, portSend);
            			if (seq!=0){serverSocket.send(Packet);}
            			continue receive;
                    }	
                    while((ChkSum|chkVal)==0xFFFF)//checking the integrity of the data.
                    {
                    	if((seq==expSeq))
                    		{
                    		ackSeq[0]=(byte)((seq)&0xFF);
                    		System.out.println("Sending Ack for the receivied packet......");//Sending Ack for current packet.
                			expSeq++;
                        	imageFile.write(strReceivedata);//writing the received data into the file/sending data to the application.
                    		}
                    	else
                    	{
                    		ackSeq[0]=(byte)((seq)&0xFF);
                    		System.out.println("Sending the Duplicate ack for previous packet.....");//Duplicate Ack;
                    		//chkSumCal=chkSum(ackSeq);
                			//sendData=create_Packet(seq,chkSumCal);
                    		j++;
                    	}
                    	//if(r<30){
            				//portSend=1500;}
                    	Packet = new DatagramPacket(ackSeq, ackSeq.length, IpAddress, portSend);
                    	serverSocket.send(Packet);//requesting for next packet.
                    	//System.out.println(new String(strReceivedata));
                    	continue receive;
                    }	
            }
            System.out.println("Completely received the data from client" );
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
