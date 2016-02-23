#!/usr/bin/env python

#Next two lines are to include parent directory for testing
import sys, time, os, subprocess
import socket
import thread
import select
import json
sys.path.append("..")
# Remove them normally

# PGW client - Gx protocol for tests with PCRF simulator

from libDiameter import *

def stoppdp(msid,apn):
	CCR_avps=[ ]
	CCR_avps.append(encodeAVP('Session-Id',ORIGIN_HOST+";"+apn+";"+msid))
	CCR_avps.append(encodeAVP('Auth-Application-Id',16777238))
	CCR_avps.append(encodeAVP('Origin-Host', ORIGIN_HOST))
	CCR_avps.append(encodeAVP('Origin-Realm', ORIGIN_REALM))
	CCR_avps.append(encodeAVP('Destination-Realm', DEST_REALM))
	CCR_avps.append(encodeAVP('CC-Request-Type', 3))
	CCR_avps.append(encodeAVP('CC-Request-Number',1))
	CCR=HDRItem()
	#setFlags(CER,DIAMETER_HDR_PROXIABLE)
	# Set command code
	CCR.cmd=dictCOMMANDname2code('Credit-Control')
	CCR.appId=16777238
	# Set Hop-by-Hop and End-to-End
	initializeHops(CCR)
	setFlags(CCR,DIAMETER_HDR_PROXIABLE)
	msg=createReq(CCR,CCR_avps)
	# send data
	Conn.send(msg.decode('hex'))	
	
def startpdp(msid,apn,ip):
	CCR_avps=[ ]
	CCR_avps.append(encodeAVP('Session-Id',ORIGIN_HOST+";"+apn+";"+msid))
	CCR_avps.append(encodeAVP('Auth-Application-Id',16777238))
	CCR_avps.append(encodeAVP('Origin-Host', ORIGIN_HOST))
	CCR_avps.append(encodeAVP('Origin-Realm', ORIGIN_REALM))
	CCR_avps.append(encodeAVP('Destination-Realm', DEST_REALM))
	CCR_avps.append(encodeAVP('CC-Request-Type', 1))
	CCR_avps.append(encodeAVP('CC-Request-Number',0))
	CCR_avps.append(encodeAVP('Framed-IP-Address',ip))
	CCR_avps.append(encodeAVP('Subscription-Id',[encodeAVP('Subscription-Id-Type',0),encodeAVP('Subscription-Id-Data',msid)]))
	CCR_avps.append(encodeAVP('Called-Station-Id',apn))
	CCR=HDRItem()
	#setFlags(CER,DIAMETER_HDR_PROXIABLE)
	# Set command code
	CCR.cmd=dictCOMMANDname2code('Credit-Control')
	CCR.appId=16777238
	# Set Hop-by-Hop and End-to-End
	initializeHops(CCR)
	setFlags(CCR,DIAMETER_HDR_PROXIABLE)
	msg=createReq(CCR,CCR_avps)
	# send data
	Conn.send(msg.decode('hex'))


def handle_cmd(srv):
	conn,address=srv.accept()
	while True:
		try:
			received = conn.recv(1024)
			jsonObject = json.loads(received)
			action = jsonObject['action']
			msid = jsonObject['msid']
			apn = jsonObject['apn']			
			if action=="pdpstart":
				ip = jsonObject['ip']
				client_list[ORIGIN_HOST+";"+apn+";"+msid]=conn
				startpdp(msid,apn,ip)
			elif action=="pdpstop":
				stoppdp(msid,apn)
		except:
			break

def handle_gx(conn):
	received = conn.recv(1024)
	msg=received.encode('hex')
	H=HDRItem()
	stripHdr(H,msg)
	avps=splitMsgAVPs(H.msg)
	for avp in avps:
		 print "Decoded AVP",decodeAVP(avp)	
	if H.cmd==280:
		DWA_avps=[ ]
		DWA_avps.append(encodeAVP('Origin-Host', ORIGIN_HOST))
		DWA_avps.append(encodeAVP('Origin-Realm', ORIGIN_REALM))
		DWA_avps.append(encodeAVP('Result-Code', 2001))
		DWA=HDRItem()
		DWA.cmd=H.cmd
		DWA.appId=H.appId
		DWA.HopByHop=H.HopByHop
		DWA.EndToEnd=H.EndToEnd
		ret=createRes(DWA,DWA_avps)
		conn.send(ret.decode("hex"))
	elif H.cmd==272:
		CCA_SESSION=findAVP("Session-Id",avps)
		rc=findAVP("Result-Code",avps)
		data = {}
		data['rc'] = rc
		json_data = json.dumps(data)
		client_list[CCA_SESSION].send(json_data+"\n")
		
if __name__ == '__main__':

# SET THIS TO YOUR PCRF SIMULATOR IP/PORT

    HOST="10.195.83.62"
    PORT=3868	
    ORIGIN_HOST="qostools.xl.co.id"
    ORIGIN_REALM="xl.co.id"
    DEST_REALM="sapctest.lte.xl.co.id"
    DEST_HOST="pcrf.sapctest.lte.xl.co.id"
    IDENTITY="1234567890" # This is msisdn of user in SPR DB


    Conn=Connect(HOST,PORT)
    

LoadDictionary("../dictDiameter.xml")


###### FIRST WE CREATE CER and receive CEA ###########################

# Let's build CER
CER_avps=[ ]
CER_avps.append(encodeAVP('Origin-Host', ORIGIN_HOST))
CER_avps.append(encodeAVP('Origin-Realm', ORIGIN_REALM))
CER_avps.append(encodeAVP('Host-IP-Address', '10.195.84.157'))
CER_avps.append(encodeAVP('Vendor-Id', '193'))
CER_avps.append(encodeAVP('Product-Name', 'QoSTools'))
CER_avps.append(encodeAVP('Origin-State-Id', 15))
CER_avps.append(encodeAVP('Supported-Vendor-Id', 10415))
CER_avps.append(encodeAVP('Vendor-Specific-Application-Id', [encodeAVP('Vendor-Id',10415),encodeAVP('Auth-Application-Id',16777238)]))
CER_avps.append(encodeAVP('Firmware-Revision', 221842434))  
# Create message header (empty)
CER=HDRItem()
# Set command code
CER.cmd=dictCOMMANDname2code('Capabilities-Exchange')
# Set Hop-by-Hop and End-to-End
initializeHops(CER)
# Add AVPs to header and calculate remaining fields
msg=createReq(CER,CER_avps)
# msg now contains CER Request as hex string

# send data
Conn.send(msg.decode('hex'))
# Receive response
received = Conn.recv(1024)

# Parse and display received CEA ANSWER
print "="*30
print "THE CEA ANSWER IS:"

msg=received.encode('hex')
print "="*30
H=HDRItem()
stripHdr(H,msg)
avps=splitMsgAVPs(H.msg)
cmd=dictCOMMANDcode2name(H.flags,H.cmd)
if cmd==ERROR:
 print 'Unknown command',H.cmd
else:
 print cmd
 print "Hop-by-Hop=",H.HopByHop,"End-to-End=",H.EndToEnd,"ApplicationId=",H.appId
 print "="*30
 for avp in avps:
    print "Decoded AVP",decodeAVP(avp)
    print "-"*30    

sock_list=[]	
client_list={}
CMD_HOST = "localhost"
CMD_PORT = 1111	
MAX_CLIENTS=5
CMD_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# fix "Address already in use" error upon restart
CMD_server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
CMD_server.bind((CMD_HOST, CMD_PORT))  
CMD_server.listen(MAX_CLIENTS)
sock_list.append(CMD_server)	
sock_list.append(Conn)
while True:
	try:
		read, write, error = select.select(sock_list,[],[],1)
	except:
		print "break"
		break
	for r in read:
		if r==Conn:
			thread.start_new_thread(handle_gx,(r,))
		elif r==CMD_server:
			thread.start_new_thread(handle_cmd,(r,))

