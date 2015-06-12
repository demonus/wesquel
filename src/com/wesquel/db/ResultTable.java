package com.wesquel.db;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.wesquel.data.domain.sql.JsonSqlValueMap;
import com.wesquel.data.domain.sql.SqlTableRow;
import com.wesquel.exceptions.InvalidActionException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 6/3/15.
 */
public class ResultTable
{

	private void analyzeJSON(String alias, String json) throws IOException, InvalidActionException
	{
		JsonReader reader = new JsonReader(new StringReader(json));

		List<JsonSqlValueMap> tables = new ArrayList<>();

		reader.beginObject();

		JsonSqlValueMap jsonObject = new JsonSqlValueMap(alias);

		tables.add(jsonObject);

		readJSON(alias, reader, jsonObject, false, tables);

		jsonObject.completeActiveRow();

		for (JsonSqlValueMap table : tables)
		{
			System.out.println(table);
		}
	}

	private void readJSON(String alias, JsonReader reader, JsonSqlValueMap valueMap, boolean isArray,
						  List<JsonSqlValueMap> tables) throws
			IOException, InvalidActionException
	{
		while (reader.hasNext())
		{
			String name;

			try
			{
				name = reader.nextName();
			}
			catch (IllegalStateException ex)
			{
				name = null;
			}

			JsonToken token = reader.peek();

			if (token == JsonToken.BEGIN_OBJECT)
			{
				boolean isUnnamedObject = (name == null);

				if (isUnnamedObject)
				{
					name = alias;
				}

				System.out.println("begin object " + name);

				reader.beginObject();

				JsonSqlValueMap newTable;

				if (isArray && isUnnamedObject)
				{
					newTable = valueMap;
				}
				else
				{
					newTable = new JsonSqlValueMap(name);

					tables.add(newTable);
				}

				readJSON(alias, reader, newTable, false, tables);

				reader.endObject();

				newTable.completeActiveRow();

				System.out.println("end object " + name);

				readJSON(alias, reader, valueMap, isArray, tables);
			}
			else if (token == JsonToken.BEGIN_ARRAY)
			{
				System.out.println("begin array " + name);

				reader.beginArray();

				JsonSqlValueMap newTable = new JsonSqlValueMap(name);

				tables.add(newTable);

				readJSON(alias, reader, newTable, true, tables);

				reader.endArray();

				System.out.println("end array " + name);

				readJSON(alias, reader, valueMap, isArray, tables);
			}
			else
			{
				if (valueMap != null && name != null)
				{
					SqlTableRow row = valueMap.getActiveRow();

					Object obj;

					switch (token)
					{
					case BOOLEAN:
						obj = reader.nextBoolean();
						break;

					case NUMBER:
						obj = reader.nextDouble();

						break;

					case NULL:
						obj = null;
						break;

					default:
						obj = reader.nextString();
					}

					row.addValue(name, obj, token);

					System.out.println(name + " = " + obj);
				}
				else
				{
					System.out.println("valueMap = " + valueMap + "; name = " + name);
				}
			}
		}
	}


	public static void main(String[] args) throws IOException, InvalidActionException
	{
		String json = "{\n \"myTest\": {\"test\": 123}," +
				" \"resultCount\":2,\n" +
				" \"results\": [\n" +
				"{\"wrapperType\":\"track\", \"kind\":\"song\", \"artistId\":148662, \"collectionId\":528436018, " +
				"\"trackId\":528437613, \"artistName\":\"LINKIN PARK\", \"collectionName\":\"Hybrid Theory\", " +
				"\"trackName\":\"In the End\", \"collectionCensoredName\":\"Hybrid Theory\", " +
				"\"trackCensoredName\":\"In the End\", \"artistViewUrl\":\"https://itunes.apple" +
				".com/us/artist/linkin-park/id148662?uo=4\", \"collectionViewUrl\":\"https://itunes.apple" +
				".com/us/album/in-the-end/id528436018?i=528437613&uo=4\", \"trackViewUrl\":\"https://itunes.apple" +
				".com/us/album/in-the-end/id528436018?i=528437613&uo=4\", \"previewUrl\":\"http://a665.phobos.apple" +
				".com/us/r1000/111/Music/a9/3f/fb/mzm.yidmhikq.aac.p.m4a\", \"artworkUrl30\":\"http://is3.mzstatic" +
				".com/image/pf/us/r30/Features/v4/fc/33/04/fc3304c7-6159-5b5d-ca08-86d2a3a0be84/dj.vrgpwamf.30x30-50" +
				".jpg\", \"artworkUrl60\":\"http://is2.mzstatic" +
				".com/image/pf/us/r30/Features/v4/fc/33/04/fc3304c7-6159-5b5d-ca08-86d2a3a0be84/dj.vrgpwamf.60x60-50" +
				".jpg\", \"artworkUrl100\":\"http://is4.mzstatic" +
				".com/image/pf/us/r30/Features/v4/fc/33/04/fc3304c7-6159-5b5d-ca08-86d2a3a0be84/dj.vrgpwamf" +
				".100x100-75" +
				".jpg\", \"collectionPrice\":10.99, \"trackPrice\":1.29, \"releaseDate\":\"2000-10-24T07:00:00Z\", " +
				"\"collectionExplicitness\":\"notExplicit\", \"trackExplicitness\":\"notExplicit\", \"discCount\":1," +
				" " +
				"\"discNumber\":1, \"trackCount\":12, \"trackNumber\":8, \"trackTimeMillis\":216294, " +
				"\"country\":\"USA\", \"currency\":\"USD\", \"primaryGenreName\":\"Alternative\", " +
				"\"radioStationUrl\":\"https://itunes.apple.com/station/idra.528437613\"}, \n" +
				"{\"wrapperType\":\"track\", \"kind\":\"song\", \"artistId\":148662, \"collectionId\":528435845, " +
				"\"trackId\":528437514, \"artistName\":\"LINKIN PARK\", \"collectionName\":\"Meteora\", " +
				"\"trackName\":\"Numb\", \"collectionCensoredName\":\"Meteora\", \"trackCensoredName\":\"Numb\", " +
				"\"artistViewUrl\":\"https://itunes.apple.com/us/artist/linkin-park/id148662?uo=4\", " +
				"\"collectionViewUrl\":\"https://itunes.apple.com/us/album/numb/id528435845?i=528437514&uo=4\", " +
				"\"trackViewUrl\":\"https://itunes.apple.com/us/album/numb/id528435845?i=528437514&uo=4\", " +
				"\"previewUrl\":\"http://a727.phobos.apple.com/us/r1000/074/Music/73/47/20/mzm.scadeqdm.aac.p.m4a\"," +
				" " +
				"\"artworkUrl30\":\"http://is3.mzstatic" +
				".com/image/pf/us/r30/Features/v4/3e/cc/11/3ecc118b-f768-5c88-3b75-1d2130661d9b/dj.rxzrauer.30x30-50" +
				".jpg\", \"artworkUrl60\":\"http://is2.mzstatic" +
				".com/image/pf/us/r30/Features/v4/3e/cc/11/3ecc118b-f768-5c88-3b75-1d2130661d9b/dj.rxzrauer.60x60-50" +
				".jpg\", \"artworkUrl100\":\"http://is1.mzstatic" +
				".com/image/pf/us/r30/Features/v4/3e/cc/11/3ecc118b-f768-5c88-3b75-1d2130661d9b/dj.rxzrauer" +
				".100x100-75" +
				".jpg\", \"collectionPrice\":10.99, \"trackPrice\":1.29, \"releaseDate\":\"2003-03-24T08:00:00Z\", " +
				"\"collectionExplicitness\":\"notExplicit\", \"trackExplicitness\":\"notExplicit\", \"discCount\":1," +
				" " +
				"\"discNumber\":1, \"trackCount\":13, \"trackNumber\":13, \"trackTimeMillis\":187508, " +
				"\"country\":\"USA\", \"currency\":\"USD\", \"primaryGenreName\":\"Alternative\", " +
				"\"radioStationUrl\":\"https://itunes.apple.com/station/idra.528437514\"}] \n}";


		ResultTable resultTable = new ResultTable();

		resultTable.analyzeJSON("itunes", json);
	}
}
