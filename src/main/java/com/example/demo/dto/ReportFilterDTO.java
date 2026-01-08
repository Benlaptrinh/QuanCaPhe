
package com.example.demo.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class ReportFilterDTO {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fromDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate toDate;
	
	private String type;

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
