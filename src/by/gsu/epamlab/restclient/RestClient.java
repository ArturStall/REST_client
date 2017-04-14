package by.gsu.epamlab.restclient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import by.gsu.epamlab.bean.Chapter;
import by.gsu.epamlab.bean.Document;

public class RestClient {
	static final String link = "http://localhost:8080/ApacheCXF_REST_Server/documents";
	static final String post = "POST";
	static final String get = "GET";
	static final String put = "PUT";
	static final String delete = "DELETE";
	static HttpURLConnection connection = null;
	static Scanner sc = null;
	static OutputStream os;	
	static StringWriter upload = new StringWriter();
	static StringWriter update = new StringWriter();
	static int updateCount = 3;
	static int deleteCount = 5;

	public static void main(String[] args) {
		Chapter chapUddate = new Chapter(0, 777777777, 99999999);
		Document doc = createDoc();
		JAXBContext contextDoc;
		JAXBContext contextChap;
		try {
			contextDoc = JAXBContext.newInstance(Document.class);
			contextDoc.createMarshaller().marshal(doc, upload);
			contextChap = JAXBContext.newInstance(Chapter.class);
	        contextChap.createMarshaller().marshal(chapUddate, update);
		} catch (JAXBException e) {
			System.err.println(e);
		}
		System.out.println("Document before send:\n" + upload.toString() + "\n___________________________________________________________________________________________\n");
		methodPOST(upload.toString());
		methodGET();
		for(int i = 0; i < updateCount; i++) {
			methodPUT(update.toString());
		}
		System.out.println("\n___________________________________________________________________________________________");
		methodGET();
		for(int i = 0; i < deleteCount; i ++) {
			methodDELETE();
			methodGET();
		}
		methodPUT(update.toString());
		methodGET();
		methodDELETE();
		methodGET();
	}
	
	public static void methodPOST(String upload) {
		connection = getConnection();
		try {
			connection.setRequestMethod(post);
			os = connection.getOutputStream();
			os.write(upload.getBytes());
			os.flush();
			System.out.println("POST --> " + connection.getResponseCode() + " " + connection.getResponseMessage() + "\n");
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			connection.disconnect();
		}
	}
	
	public static void methodGET() {
		connection = getConnection();
		String response;
		try {			
			connection.setRequestMethod(get);
			response = null;
			if(connection.getResponseCode() != 200) {
				sc = new Scanner(connection.getErrorStream());
				response = "Error!";
			} else {
				sc = new Scanner(connection.getInputStream());
				response = "Response:";
			}
			System.out.println("GET --> " + connection.getResponseCode() + " " + connection.getResponseMessage());
			System.out.println(response);
			while (sc.hasNextLine()) {
				System.out.println(sc.nextLine() + "\n___________________________________________________________________________________________\n\n");
			}			
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			sc.close();
			connection.disconnect();
		}
	}

	public static void methodPUT(String update) {
		connection = getConnection();
		try {			
			connection.setRequestMethod(put);
			os = connection.getOutputStream();
			os.write(update.getBytes());
			os.flush();
			System.out.println("Update --> " + connection.getResponseCode() + " " + connection.getResponseMessage() + "\n");
		} catch (IOException e) {
			System.err.println(e);
		} finally {			
			connection.disconnect();
		}
	}
	
	public static void methodDELETE() {
		connection = getConnection();
		try {
			connection.setRequestMethod(delete);
			System.out.println("DELETE --> " + connection.getResponseCode() + " " + connection.getResponseMessage() + "\n");
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			connection.disconnect();
		}
	}
	
	public static HttpURLConnection getConnection() {
		try {
			connection = (HttpURLConnection) new URL(link).openConnection();
		} catch (IOException e) {
			System.err.println(e);
		}
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		return connection;
	}
	
	public static Document createDoc() {
		List<Chapter> chapters = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			chapters.add(new Chapter(i, i*2, i*2+1));
		}
		return new Document(0, "doc1", chapters);
	}
}
