package ee.tkasekamp.vickywaranalyzer.util;

import ee.tkasekamp.vickywaranalyzer.core.Country;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Localisation {

	private static Map<String, String> countryMap = new HashMap<>();

	/**
	 * Main method of this class. Manages the reading from csv
	 */
	public static void readLocalisation(String installPath,
										ArrayList<Country> countryList) {
		// Emptying the countryMap so all a new savegame is read only with that
		// file's countries in map
		countryMap.clear();
		// Adding the countries to map
		for (Country country : countryList) {
			countryMap.put(country.getTag(), country.getTag());
		}

		try {
			List<String> loclist = getLocalisationFiles(installPath);

			for (String string : loclist) {
				readCSV(installPath + "/localisation/" + string, countryList);
			}

		} catch (NullPointerException | IOException e) {

		}

	}

	/**
	 * Reads the given file and for any given line checks if the tag is equal to
	 * the one in countryList.
	 */
	private static void readCSV(String filename, ArrayList<Country> countryList)
			throws IOException {
		/* The same reader as in SaveGameReader */
		InputStreamReader reader = new InputStreamReader(new FileInputStream(
				filename), "ISO8859_1"); // This encoding seems to work for ö
		// and ü
		BufferedReader scanner = new BufferedReader(reader);

		String line;
		while ((line = scanner.readLine()) != null) {
			String[] dataArray = line.split(";"); // Splitting the line

			// Checking for every country in map if the first part of line is
			// equal to key
			// If it is, set it as value
			// Every time a match is found, the matching item is removed from
			// the map so next time less checks are required
			Iterator<Entry<String, String>> it = countryMap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it
						.next();
				if (entry.getKey().equals(dataArray[0])) {
					entry.setValue(dataArray[1]);
					mapCleaner(entry, countryList);
					it.remove();

				}
			}

		}
		// Close the file once all data has been read.
		scanner.close();

	}

	/**
	 * This method gives the entry value to one of the countries in the
	 * countyList. After this the entry can be removed from map. I think doing
	 * it like this is most efficient in terms of "if" checks required.
	 */
	synchronized private static void mapCleaner(
			Map.Entry<String, String> entry, ArrayList<Country> countryList) {
		countryList.stream().filter(country -> country.getTag().equals(entry.getKey())).forEach(country -> {
			country.setOfficialName(entry.getValue());
		});
	}

	private static List<String> getLocalisationFiles(String path)
			throws NullPointerException {
		List<String> locList = new ArrayList<>();

		File folder = new File(path + "/localisation");
		File[] listOfFiles = folder.listFiles();
		String file;

		for (File listOfFile : listOfFiles) {
			if (listOfFile.isFile()) {
				file = listOfFile.getName();
				if (file.endsWith(".csv") || file.endsWith(".CSV")) {
					locList.add(file);
				}
			}
		}

		return locList;

	}
}
