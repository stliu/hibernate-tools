package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.util.HashSet;
import java.util.Set;

/**
 * todo: describe Address
 *
 * @author Steve Ebersole
 */
public class Address2 {
	private Long id;
	private Set lines = new HashSet();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set getLines() {
		return lines;
	}

	public void setLines(Set lines) {
		this.lines = lines;
	}
}
