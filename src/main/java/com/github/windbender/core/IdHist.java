package com.github.windbender.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.github.windbender.domain.Identification;
import com.github.windbender.domain.Species;

public class IdHist extends ArrayList<IdHistEntry> {

	public IdHist() {
		super();
	}
	public IdHist(List<Identification> ids) {
		TreeMap<Long,Integer> map  = new TreeMap<Long,Integer>();
		for(Identification id: ids) {
			Species species = id.getSpeciesIdentified();
			Long l = species.getId();
			Integer cnt = map.get(l);
			if(cnt == null) {
				cnt = new Integer(0);
				map.put(l,cnt);
			}
			cnt = cnt + 1;
			map.put(l,cnt);
		}
		for(Entry<Long, Integer> e: map.entrySet()) {
			add(new IdHistEntry(e.getKey(),e.getValue()));
		}
	}

}
