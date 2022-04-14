/*
 * Copyright (c) 2022 Tander, All Rights Reserved.
 */

package ru.task.services;

import ru.task.entity.*;
import ru.task.group.Group;

import java.io.*;
import java.util.*;

public class DataProcessing {

	private final  String SEP = File.separator;
	private final  String FILEPATH = System.getProperty("user.dir") + SEP + "src" +
		SEP + "main" + SEP + "resources" + SEP + "lng.csv";

	private  Set<MultiEntity> uniqueStrings = new HashSet<>();

	private Map<Entity, ArrayList<MultiEntity>> containers = new HashMap<>();

	private int countGroup = 0;
	private Map<Integer, ArrayList<Group>> orderedGroup = new TreeMap<>(Collections.reverseOrder());

	public void readFile() {
		try (
			BufferedReader reader = new BufferedReader(new FileReader(FILEPATH))) {
			String line;
			while ((line = reader.readLine()) != null) {
				filterData(line.replace("\"", "").split(";", -1));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void filterData(String[] values) {
		if (values.length == 3) {
			MultiEntity multiEntity = new MultiEntity(values);
			if (!uniqueStrings.contains(multiEntity) && multiEntity.isLegit()) {
				uniqueStrings.add(multiEntity);
				primaryGrouping(multiEntity);
			}
		}
	}


	private  void primaryGrouping(MultiEntity multiEntity) {
		for (Entity entity : multiEntity.getLegitEntities()) {
			ArrayList<MultiEntity> v = containers.getOrDefault(entity, new ArrayList<>());
			v.add(multiEntity);
			containers.put(entity, v);
		}
	}

	public  void clearGroupWithoutPair() {
		Map<Entity, ArrayList<MultiEntity>> newContains = new HashMap<>();
		for (Map.Entry<Entity, ArrayList<MultiEntity>> entry : containers.entrySet()) {
			if (entry.getValue().size() > 1) {
				newContains.put(entry.getKey(), entry.getValue());
			}
		}
		containers = newContains;
	}

	public  void finalGrouping() {
		additionalGrouping();

		for (Map.Entry<Entity, ArrayList<MultiEntity>> entry : containers.entrySet()) {
			ArrayList<Group> v = orderedGroup.getOrDefault(entry.getValue().size(), new ArrayList<>());
			Group group = new Group(entry.getValue());
			v.add(group);
			orderedGroup.put(group.size(), v);
			countGroup++;
		}
	}

	private  void additionalGrouping() {
		Map<MultiEntity, Integer> multiEntityCount = new HashMap<>();

		for (Map.Entry<Entity, ArrayList<MultiEntity>> entry : containers.entrySet()) {
			ArrayList<MultiEntity> entities = entry.getValue();
			for (MultiEntity multiEntity : entities) {
				int value = multiEntityCount.getOrDefault(multiEntity, 0);
				multiEntityCount.put(multiEntity, value + 1);
			}
		}


		List<Set<MultiEntity>> multiEntitySubGroup = new ArrayList<>();
		for (
			Map.Entry<MultiEntity, Integer> entry : multiEntityCount.entrySet()) {
			if (entry.getValue() > 1) {
				Set<MultiEntity> set = new HashSet<>();
				for (Entity entity : entry.getKey().getLegitEntities()) {
					if (containers.containsKey(entity)) {
						set.addAll(containers.remove(entity));
					}
				}
				if (set.size() > 1)
					multiEntitySubGroup.add(set);
			}
		}

		for (Set<MultiEntity> subGroup : multiEntitySubGroup) {
			ArrayList<Group> v = orderedGroup.getOrDefault(subGroup.size(), new ArrayList<>());
			Group group = new Group(new ArrayList<>(subGroup));
			v.add(group);
			orderedGroup.put(group.size(), v);
			countGroup++;
		}
	}
	public  void print() {
		System.out.println("Кол-во групп " + countGroup);
		int count = 1;
		for (Map.Entry<Integer, ArrayList<Group>> entry : orderedGroup.entrySet()) {
			for (Group group : entry.getValue()) {
				System.out.println("Группа " + count + "\n");
				System.out.println(group);
				count++;
			}
		}
	}
}