package com.github.windbender.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.github.windbender.domain.Identification;
import com.github.windbender.domain.Species;

public class NameHist extends ArrayList<NameHistEntry> {

	public NameHist(List<Identification> ids) {
		TreeMap<String,Integer> map = new TreeMap<String,Integer>();
		for(Identification id: ids) {
			Species species = id.getSpeciesIdentified();
			String l = species.getName();
			Integer cnt = map.get(l);
			if(cnt == null) {
				cnt = new Integer(0);
				map.put(l,cnt);
			}
			cnt = cnt + 1;
			map.put(l,cnt);
		}
		for(Entry<String, Integer> e: map.entrySet()) {
			add(new NameHistEntry(e.getKey(),e.getValue()));
		}
	}

}
