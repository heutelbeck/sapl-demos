package io.sapl.demo.geo.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.demo.geo.domain.PilData;
import io.sapl.demo.geo.domain.PilMetaInf;
import io.sapl.demo.geo.domain.PilPassenger;
import io.sapl.demo.geo.domain.PilPaxInf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PilDataConstructor {
	private static final String AC_TYPE = "B748";
	private static final String M = "M"; // male
	private static final String F = "F"; // female
	private static final String FILE_PATH = "src/main/resources/";
	private static final String FILE_MALE = "male_names.txt";
	private static final String FILE_FEMALE = "female_names.txt";
	private static final String FILE_SURNAMES = "surnames.txt";
	private static final String COMMA = ",";
	private static final String PIL_BUILD_ERR = "An error occurred while creating the PIL.";
	public static final String DATE_FORMAT = "dd.MM.yyyy";
	private static final String[] SPECIALS = { "-", "-", "-", "-", "HON", "SEN", "FTL" };
	private static final int F_MAX = 8; // max passengers in first class
	private static final int C_MAX = 80; // max passengers in business class
	private static final int E_MAX = 32; // max passengers in premium economy class
	private static final int Y_MAX = 244; // max passengers in economy class
	private static final int TTL_MAX = F_MAX + C_MAX + E_MAX + Y_MAX;
	private static final int META = 0;
	private static final int RESTRICTED = 1;
	private static final int CONFIDENTIAL = 2;
	private static final int MALE = 0;
	private static final int GENDER = 2;
	private static final long BDATE_YEAR_BEGIN = -946771200000L; // 01.01.1940
	private static final long BDATE_TIMEFRAME = 75L * 365 * 24 * 60 * 60 * 1000; // 75 years
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private String[] firstNamesMale;
	private String[] firstNamesFemale;
	private String[] surnames;
	private SecureRandom rand;

	private PilData data;
	private int classification;
	private String depAp;
	private String arrAp;
	private String fltNo;
	private String date;
	private int actNumPax;

	public PilDataConstructor(int level, String from, String to, String flightNumber, String dateOfFlight, int minSeats)
			throws IOException {
		rand = new SecureRandom();
		data = new PilData();
		actNumPax = minSeats + rand.nextInt(TTL_MAX - minSeats);
		classification = level;
		depAp = from;
		arrAp = to;
		fltNo = flightNumber;
		date = dateOfFlight;

		firstNamesMale = getNameList(FILE_MALE);
		firstNamesFemale = getNameList(FILE_FEMALE);
		surnames = getNameList(FILE_SURNAMES);
	}

	public String getData() {
		if (classification >= META) {
			data.setMetaData(constructMetaInf());
		}
		if (classification >= RESTRICTED) {
			data.setPaxData(constructRandomPaxInf());
		}
		if (classification >= CONFIDENTIAL) {
			data.setPassengers(constructRandomPaxDetails());
		}

		try {
			return MAPPER.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			LOGGER.error(e.toString());
			return PIL_BUILD_ERR;
		}
	}

	private PilMetaInf constructMetaInf() {
		return PilMetaInf.builder().arrAp(arrAp).depAp(depAp).fltNo(fltNo).date(date).acType(AC_TYPE)
				.classification(classification).build();
	}

	private PilPaxInf constructRandomPaxInf() {
		// Creates a random distribution of the passengers among the different classes
		int tempPaxCount = actNumPax;
		int fAct;
		int cAct;
		int eAct;
		int yAct;

		// First class
		if ((fAct = rand.nextInt(F_MAX)) <= tempPaxCount) {
			tempPaxCount -= fAct;
		} else {
			fAct = 0;
		}

		// Business class
		if ((cAct = rand.nextInt(C_MAX)) <= tempPaxCount) {
			tempPaxCount -= fAct;
		} else {
			cAct = 0;
		}

		// Premium Economy class
		if ((eAct = rand.nextInt(E_MAX)) <= tempPaxCount) {
			tempPaxCount -= fAct;
		} else {
			eAct = 0;
		}

		// Economy class
		if (tempPaxCount > Y_MAX) {
			yAct = Y_MAX;
			actNumPax = fAct + cAct + eAct + yAct;
		} else {
			yAct = tempPaxCount;
		}

		return PilPaxInf.builder().fAct(fAct).fMax(F_MAX).cAct(cAct).cMax(C_MAX).eAct(eAct).eMax(E_MAX).yAct(yAct)
				.yMax(Y_MAX).build();
	}

	private PilPassenger[] constructRandomPaxDetails() {
		PilPassenger[] pax = new PilPassenger[actNumPax];
		for (int i = 0; i < actNumPax; i++) {
			int gender = rand.nextInt(GENDER);
			pax[i] = PilPassenger.builder().name(randomName(gender)).bdate(randomBdate()).gender(gender == MALE ? M : F)
					.seat(String.valueOf(i)).special(SPECIALS[rand.nextInt(SPECIALS.length - 1)]).build();
		}
		return pax;
	}

	private String randomBdate() {
		// random birthday between 01.01.1940 and 01.01.2015
		long ms = BDATE_YEAR_BEGIN + (long) (rand.nextDouble() * BDATE_TIMEFRAME);
		return new SimpleDateFormat(DATE_FORMAT, Locale.GERMAN).format(new Date(ms));
	}

	private String randomName(int gender) {
		StringBuilder name = new StringBuilder();
		name.append(surnames[rand.nextInt(surnames.length - 1)]).append(COMMA);
		if (gender == MALE) {
			name.append(firstNamesMale[rand.nextInt(firstNamesMale.length - 1)]);
		} else {
			name.append(firstNamesFemale[rand.nextInt(firstNamesFemale.length - 1)]);
		}
		return name.toString();
	}

	private static String[] getNameList(String file) throws IOException {
		InputStreamReader fileReader = new InputStreamReader(
				new FileInputStream(FILE_PATH + FilenameUtils.getName(file)), StandardCharsets.UTF_8);

		List<String> result = new ArrayList<>();
		try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				result.add(line);
			}
		}
		return result.toArray(new String[result.size()]);
	}

}
