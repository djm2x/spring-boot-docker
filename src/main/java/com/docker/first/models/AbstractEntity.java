package com.docker.first.models;

import java.io.Serializable;

public abstract class AbstractEntity implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public interface ValidationMinimal {
	};

	public interface ValidationActive {
	};

}
