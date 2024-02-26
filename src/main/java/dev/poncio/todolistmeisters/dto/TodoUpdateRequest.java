package dev.poncio.todolistmeisters.dto;

import lombok.Data;

@Data
public class TodoUpdateRequest {

	private String title;
	private String description;

}
