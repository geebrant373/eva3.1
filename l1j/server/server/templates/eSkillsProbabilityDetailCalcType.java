package l1j.server.server.templates;

public enum eSkillsProbabilityDetailCalcType {
	NONE(0), 
	STONE(1), 
	SLEEP(2), 
	FREEZE(3), 
	HOLD(4), 
	STUN(5);

	private int value;

	eSkillsProbabilityDetailCalcType(int val) {
		value = val;
	}

	public int toInt() {
		return value;
	}

	public boolean equals(eSkillsProbabilityDetailCalcType v) {
		return value == v.value;
	}

	public static eSkillsProbabilityDetailCalcType fromString(String type) {
		switch (type) {
		case "없음":
			return NONE;
		case "석화":
			return STONE;
		case "수면":
			return SLEEP;
		case "동빙":
			return FREEZE;
		case "홀드":
			return HOLD;
		case "스턴":
			return STUN;
		default:
			throw new IllegalArgumentException(String.format("invalid arguments eSkillsProbabilityDetailCalcType, %d", type));
		}
	}
}
