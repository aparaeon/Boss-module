package com.raduvoinea.file_manager.dto.interface_serialization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CustomObject2 implements CustomInterface {

	public String data;
	public String otherData;

}
