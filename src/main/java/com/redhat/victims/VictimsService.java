package com.redhat.victims;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

/**
 * 
 * This implements a basic wrapper for the victims web service. Provides basic
 * interactions with the REST interface.
 * 
 * @author gcmurphy
 * @author abn
 * 
 */
public class VictimsService {
	public static String VICTIMS_URI = "https://victims-websec.rhcloud.com";
	public static final String VICTIMS_SERVICE = "/service/v2";

	/**
	 * 
	 * Get all new records after a given date.
	 * 
	 * @param since
	 *            The date from when updates are required for.
	 * @return
	 * @throws IOException
	 */
	public static RecordStream updates(Date since) throws IOException {
		return fetch(since, "update");
	}

	/**
	 * 
	 * Get all records removed after a given date.
	 * 
	 * @param since
	 *            The date from when removed records are required for.
	 * @return
	 * @throws IOException
	 */
	public static RecordStream removed(Date since) throws IOException {
		return fetch(since, "remove");
	}

	/**
	 * 
	 * Work horse method that provides a {@link RecordStream} wraped from a
	 * response received from the server.
	 * 
	 * @param since
	 *            The date from when removed records are required for.
	 * @param type
	 *            The service type. To be used as ${base-uri}/${service}/%{type}
	 * @return
	 * @throws IOException
	 */
	protected static RecordStream fetch(Date since, String type)
			throws IOException {
		SimpleDateFormat fmt = new SimpleDateFormat(VictimsRecord.DATE_FORMAT);
		String uri = String.format("%s/%s/%s/%s", VICTIMS_URI, VICTIMS_SERVICE,
				type, fmt.format(since));
		return new RecordStream(uri);
	}

	/**
	 * This provides a simple ObjectStream like implementation for wrapping
	 * streamed responses from the server.
	 * 
	 * @author abn
	 * 
	 */
	public static class RecordStream {
		protected JsonReader json;
		protected InputStream in;
		protected Gson gson;

		public RecordStream(String uri) throws IOException {
			this.gson = new GsonBuilder().setDateFormat(
					VictimsRecord.DATE_FORMAT).create();
			this.in = new URL(uri).openStream();
			this.json = new JsonReader(new InputStreamReader(in, "UTF-8"));
			this.json.beginArray();
		}

		/**
		 * 
		 * @return The next available {@link VictimsRecord}. If none available
		 *         returns null.
		 * @throws IOException
		 */
		public VictimsRecord getNext() throws IOException {
			if (hasNext()) {
				json.beginObject();
				json.nextName(); // discard fields
				VictimsRecord v = gson.fromJson(json, VictimsRecord.class);
				System.out.println(v.toString());
				json.endObject();
				return v;
			}
			return null;
		}

		/**
		 * Checks if the internal {@link JsonReader} has any more json strings
		 * available. If not we end the json array and close the response
		 * stream.
		 * 
		 * @return <code>true</code> if more records can be read, else returns
		 *         <code>false</code>.
		 * @throws IOException
		 */
		public boolean hasNext() throws IOException {
			if (!json.hasNext()) {
				json.endArray();
				IOUtils.closeQuietly(in);
				return false;
			}
			return true;
		}
	}

	public static void main(String[] args) throws IOException, ParseException {
		// DEBUG CODE
		// jdk 1.7 does not like name errors
		// System.setProperty("jsse.enableSNIExtension", "false");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		RecordStream rs = VictimsService.updates(sdf.parse("01/01/2010"));
		while (rs.hasNext()) {
			System.out.println(rs.getNext());
		}

	}

}