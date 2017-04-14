
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;
import java.nio.ByteBuffer;
//import java.util.Arrays;
//import java.util.Random;
//import java.util.Scanner;

import javax.imageio.ImageIO;

class Delay implements Runnable{
	Thread t;
	
	void someThread(){
		System.out.println("Start Time");
		t= new Thread(this);
		t.start();
		
	}
	public void run(){
		
		t.sleep(1000);
		UDPClient rec = new UDPClient();
		
	}
}
public class UDPClient {

    private static final boolean DEBUG = true;
    
    	private static byte[] calChecksum(byte[] pck)//Method for calculating Checksum.
    		{	
    		byte [] chkSum= new byte[2];
    		int a=0;
    		long inData=0x0000;
    		long dataSum=0x0000;
    	    	while(a<pck.length)	
    	    	{
    	    	inData=(((pck[a]<<8)& 0xFF00)|((pck[++a])& 0xFF));
    	    	dataSum=(dataSum+inData);
    	    		if((dataSum & 0xFFFF0000)>0)//Checking for overflow/carry bit;
    	    		{
    	    		dataSum =dataSum & 0xFFFF;//subtracting the carry bit;
    	    		dataSum=dataSum+0x1;//adding the carry overflow.
    	    		}
    	    	a++;
    	    	}
    	    	System.out.println("Chksum "+dataSum);
    	    	dataSum=(~dataSum & 0xFFFF);
    	    	chkSum[0]=(byte)(dataSum>>8);
    	    	chkSum[1]=(byte)(dataSum & 0xFF);
    	    	System.out.println("CheckSum value: "+(dataSum));
    	    	//long l=(((chkSum[0]<<8)& 0xFF00)|(chkSum[1]&0xFF));
    	    	return chkSum;
    		} 
    	private static  void delay()//Delay method.
    	{
    		long startTime= System.currentTimeMillis();
    		long endTime=startTime+1000;
    		while(System.currentTimeMillis()<endTime)
    		{
    			continue;
    		}
    		//return true;
    	}
    	private static byte[] create_Packet(int seq,byte[]chkCalSum,byte[] sendData )//Method to create packet.
    	{
    		byte[] packet= new byte[1027];
    		 packet[0]= (byte)(seq & 0xFF);
 			System.arraycopy(chkCalSum, 0, packet, 1, chkCalSum.length);
 			System.arraycopy(sendData, 0, packet, chkCalSum.length+1, sendData.length);
    		return packet;
    	}
    	
    public static void main(String[] args) throws InterruptedException {
        if (DEBUG) {
            System.out.println("Starting the client ");
        }
        new Delay();
        try {
        	byte[] someBy = new byte[1027];
        	Random randNum= new Random();
        	int i = 0;
            int j = 0;
            int seq= 0x0;//total number of sequence number 8.
            int o=0;
            int in=0;
            int k=0;
            boolean timeOut = false;
            int wnds=4;
            int z=0;
            int base=0;
            
            int n=0;
            
            byte[] chkCalSum= new byte[2];
            byte[] recAck= new byte[1];
            DatagramSocket clientSocket=new DatagramSocket();
            clientSocket.setSoTimeout(1000);
            InetAddress IpAddress = InetAddress.getByName("kushal_gowda");
            File inputFile = new File("C:\\Users\\Kushal\\workspace\\Phase5\\Scholes.jpeg");
            FileInputStream loadFile = new FileInputStream(inputFile);
            byte[] fileOut;
            fileOut = new byte[(int)inputFile.length()];
            loadFile.read(fileOut);
            Scanner inOption = new Scanner(System.in);
            byte[] sendData= new byte[1024];
            byte[] sendLen= new byte[2];
            byte[]trackSeq=new byte[4];
            int trSeq=0;
            long fileLength= ((long)inputFile.length())/1024;//Calculating the number of bytes.
           
            System.out.println("Length"+ fileLength);
            sendLen[0]=(byte)(0xFF & (fileLength>>8));
            sendLen[1]=(byte)(fileLength & 0xFF);
            DatagramPacket[] Packet1= new DatagramPacket[(int) fileLength];
            
            DatagramPacket handPacket = new DatagramPacket(sendLen, sendLen.length, IpAddress, 1556);
            clientSocket.send(handPacket);
            clientSocket.receive(handPacket);
           // rcv_Packet(sendLen);
            
            DatagramPacket Packet ;
            sendPacket: while(j<fileOut.length)
            {
            	sendData[i]=fileOut[j];
            	i++	;
            	j++;
            		if(j%1024==0)
            			{
            				while(wnds>0)
            				{
            					
            				chkCalSum=calChecksum(sendData);
            				someBy=create_Packet(seq,chkCalSum,sendData);
            				trackSeq[z]=(byte)(seq&0xFF);
            				Packet = new DatagramPacket(someBy, someBy.length, IpAddress, 1556);
            						System.out.println("Sending data to server..."); 
            						System.out.println("Sequence number is : "+seq);
            						z++;
            						seq++;
            						clientSocket.send(Packet);
            			
            				sendData= new byte[1024];
            				someBy= new byte[1027];
            				i=0;
            				wnds--;
            				if(wnds!=0){
            				continue sendPacket;}
            				}
            				if(k<1)
        					{
        						System.out.println("Please enter an option between 1 and 5");
        						o= inOption.nextInt();
        					}
            				if(o!=1 & k<1)
        					{
        						System.out.println("Please enter the error percentage ");
        						in = inOption.nextInt();
        					}
            				z=0;
            				k++;
            				wnds=4;
            	receivePacket:while(wnds>0)
            				{
            					Packet= new DatagramPacket(recAck,recAck.length,IpAddress,1556);
            					try{
            						//Thread.sleep(500);
            						clientSocket.receive(Packet);
            					}catch(SocketTimeoutException e){
            						System.out.println(" Timeout occured ,re-sending the previous packet");
            						seq=trackSeq[z];
            						j=j-((4-z)*1024);
            						wnds=4;
        							i=0;
        							z=0;
            						continue sendPacket;
            					}
            				
            				int p=(int)recAck[0];
            				System.out.println("Ack Value is "+p);
            				int r=randNum.nextInt(100);
            				switch(o){
                    					case 1:
                    						break;
                    					case 2:
                    						if(r<in)
                    						{
                    							p=randNum.nextInt(200);//Corrupting the Ack.
                    						}
                    						break;
                    					case 3:
                    							break;
                    					case 4:
                    							break;
                    					case 5:
                    							break;
                    						
                    					}
                    				
            						
            						recAck=new byte[1];
            						if(trackSeq[z]==p){System.out.println("Stored Ack "+trackSeq[z]);wnds--;z++;continue receivePacket;}
            						else
            						{
            							System.out.println("Stored Ack is wrong "+trackSeq[z]);	
            							seq=trackSeq[z];
            							j=j-((4-z)*1024);
            							wnds=4;
            							i=0;
            							z=0;
            							continue sendPacket;
            						}
            						
            				}	
            				wnds=4;
                			i=0;
                			z=0;
                			continue sendPacket;
            			}
            		
            }
            System.out.println("Sending data to server...");
            	chkCalSum=calChecksum(sendData);
            someBy=create_Packet(seq,chkCalSum,sendData);
            Packet = new DatagramPacket(someBy, someBy.length, IpAddress, 1556);
            clientSocket.send(Packet);
            clientSocket.close();
            
            if (DEBUG) {
                System.out.println("Data transmission to Server completed!!! ");
            }
                   } catch (IOException e) {
            System.out.println(e);
        }
    }
}
