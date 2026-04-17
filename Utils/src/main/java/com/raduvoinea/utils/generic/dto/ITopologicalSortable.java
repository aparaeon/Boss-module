package com.raduvoinea.utils.generic.dto;

import java.util.List;

public interface ITopologicalSortable<ID> {

	ID getID();

	List<ID> getDependencies();

}
