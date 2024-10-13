package io.sapl.geo.demo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Table("Data")
@RequiredArgsConstructor
@ToString(callSuper = true)
public class Data {

	@Id
	@Column("Id")
	private int id;
	
	@Column("Classified")
	private final int classified;
	
	@Column("Content")
	private final String content;
}
