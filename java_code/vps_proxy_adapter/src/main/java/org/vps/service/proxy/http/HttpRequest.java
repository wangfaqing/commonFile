package org.vps.service.proxy.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vps.app.util.keyValue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class HttpRequest {
	
	public static boolean headersAvailable(byte[] byteHttpRequest, int nBytelen, HttpRequestHeader header) {
		for( int i = 1; i<nBytelen; i++) {
			if (byteHttpRequest[i] == 10 && ((i+2 < nBytelen) && byteHttpRequest[i+2] == 10)) {
				if (byteHttpRequest[i-1] == 13 && byteHttpRequest[i+1] == 13) {
					header.setHeaderLen(i+1);
					return true;
				}
			}
		}
		return false; 
	}
	
	public static void parseHeader(byte[] byteHttpRequest, int nHeaderlen, HttpRequestHeader header) {
		String strHeader = new String(byteHttpRequest, 0, nHeaderlen);
		String []headers = strHeader.split("\r\n");
		if (headers != null)
		if (headers.length > 0) {
			int nOffsetUri = headers[0].indexOf(0x20);
			if (nOffsetUri == -1) {
				return ;
			}
			int nOffsetVersion = headers[0].indexOf(0x20, nOffsetUri + 1);
			header.setMethon(headers[0].substring(0, nOffsetUri));
			if (nOffsetVersion != -1){
				header.setUri(headers[0].substring(nOffsetUri + 1, nOffsetVersion));
				header.setVersion(headers[0].substring(nOffsetVersion + 1, headers[0].length()));
			} else {
				header.setUri(headers[0].substring(nOffsetUri + 1));
				header.setVersion("");
			}
		}
		for(int i = 1; i<headers.length; i++) {
			int nOffsetSplit = headers[i].indexOf(':');
			String strKey = headers[i].substring(0, nOffsetSplit);
			String strValue = headers[i].substring(nOffsetSplit+1,headers[i].length()).trim();
			header.getHeaders().add(new keyValue<String, String>(strKey, strValue));
		}
	}
	
	public static boolean isRequestComplete(ByteBuf requestData, HttpRequestHeader header) {
		if (!HttpRequest.headersAvailable(requestData.array(), requestData.writerIndex(), header)) {
			return false;
		}
		if (header.getHeaders().size() == 0) {
			HttpRequest.parseHeader(requestData.array(), header.getHeaderLen(), header);
			if (header.getHeaders().size() == 0) return false;
		}
		
		//判断conten-leng 和 trunk
		for(int i = 0; i < header.getHeaders().size(); i++) {
			String _strkey = header.getHeaders().get(i).getKey();
			String strkey = _strkey.toLowerCase();
			if (strkey.equals("content-length")) {
				String strval = header.getHeaders().get(i).getValue();
				int bodylen = Integer.parseInt(strval);
				if (bodylen > requestData.writerIndex()-header.getHeaderLen()-2) {
					return false;
				}
				return true;
			}else if (strkey.equals("transfer-encoding")) {
				int bodylen = requestData.writerIndex() - header.getHeaderLen() - 2;
				if (bodylen == 0) return false;
				
				ByteBuf buffer = Unpooled.buffer();
				buffer.writeBytes(requestData, header.getHeaderLen() + 2, bodylen);
				byte[] byteBody = buffer.array();
				boolean isFindZero = false, isFindEmpty = false;
				for(int j = 1, first= 0; j < bodylen; j++) {
					if (byteBody[j] == 10 && byteBody[j-1] == 13) {
						int offset = Integer.parseInt(new String(byteBody, first, j-1-first), 16);
						if (offset == 0)
							isFindZero = true;
						j+=offset + 2;
						first=j+1;
						if (bodylen == j+1) {
							isFindEmpty = true;
						}
					}
					if (isFindEmpty && isFindZero) {
						break;
					}
				}
				if (!isFindEmpty || !isFindZero) {
					return false;
				}
				return true;
			}
		}
		return true;
	}
	
	public static boolean isNumeric (String str) { 
	   Pattern pattern = Pattern.compile("[0-9]*"); 
	   Matcher isNum = pattern.matcher(str);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}
}

