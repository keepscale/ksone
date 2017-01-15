package org.crossfit.app.stats.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.LocalDate;
import org.joda.time.ReadablePartial;
import org.json.JSONArray;
import org.json.JSONObject;

public class StackedData {


	private Map<String, List<DateSumPair>> datas = new HashMap<>();
	

	public static class DateSumPair {
		public DateSumPair(LocalDate date, long count) {
			this.date = date;
			this.sum = count;
		}
		private LocalDate date;
		private double sum;
	}


	public void putData(String name, LocalDate startMonthInclus, long countSubscriptionAt) {
		datas.putIfAbsent(name, new ArrayList<>());
		datas.get(name).add(new DateSumPair(startMonthInclus, countSubscriptionAt));
	}
	
	public JSONArray toJson(){
		JSONArray array = new JSONArray();
		for (Entry<String, List<DateSumPair>> entry : datas.entrySet()) {
			JSONArray values = new JSONArray();
			for (DateSumPair pair : entry.getValue()) {
				JSONArray valuePair = new JSONArray();
				valuePair.put(pair.date.toDateTimeAtStartOfDay().getMillis());
				valuePair.put(pair.sum);
				values.put(valuePair);
			}
			JSONObject keyValue = new JSONObject();
			keyValue.put("key", entry.getKey());
			keyValue.put("values", values);
			array.put(keyValue);
		}
		return array;
	}
}
