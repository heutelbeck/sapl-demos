package org.demo.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.demo.model.TimeScheduleData;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class TimeScheduleService {

	private final Random rnd = new Random();

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofPattern("dd.MM.yyyy HH:mm");

	private final String[] names = new String[] { "Peter", "Alina", "Julia",
			"Brigitte", "Janosch", "Janina", "Thomas" };

	private final Set<String> doctors = new HashSet<>(Arrays.asList("Peter", "Alina", "Julia"));

	private AtomicLong instantSeconds = new AtomicLong(Instant.now().getEpochSecond());

	public Flux<TimeScheduleData> getData() {
		return Flux.<TimeScheduleData>generate(sink -> {
			final String name = randomName();
			final String title = doctors.contains(name) ? "Dr. " : "";
			sink.next(new TimeScheduleData(title, name, randomInstant()));
		}).delayElements(Duration.ofSeconds(2));
	}

	private String randomName() {
		return names[rnd.nextInt(7)];
	}

	private String randomInstant() {
		int oneDayInSeconds = 24 * 60 * 60;
		instantSeconds.getAndAdd(rnd.nextInt(oneDayInSeconds) + 60);
		final Instant instant = Instant.ofEpochSecond(instantSeconds.get());
		final LocalDateTime cet = LocalDateTime.ofInstant(instant, ZoneId.of("CET"));
		return dateTimeFormatter.format(cet);
	}

}
