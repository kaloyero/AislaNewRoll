package com.aisla.newrolit.global;

public class GreenScreenElement {

    private Integer _value;
    private Integer _attribute;

    public Integer getValue() {
		return _value;
	}

	public Integer getAttribute() {
		return _attribute;
	}

	public void setValue(Integer value) {
		this._value = value;
	}

	public void setAttribute(Integer _attribute) {
		this._attribute = _attribute;
	}

	public GreenScreenElement (Integer value, Integer attribute) {
        _value = value;
        _attribute = attribute;
    }
	
}
