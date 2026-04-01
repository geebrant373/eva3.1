package l1j.server.ParseHelper;

public class ArrangeParseeFactory {
	public static ArrangeParsee<Integer> createIntArrange(){
		return new AbstractArrangeDefaultParsee<Integer>(){
			@Override
			public void parse(int idx, String data) {
				_datas[idx] = Integer.parseInt(data.trim());
			}
		}.init(Integer.class);
	}
	
	public static ArrangeParsee<Double> createDoubleArrange(){
		return new AbstractArrangeDefaultParsee<Double>(){
			@Override
			public void parse(int idx, String data) {
				_datas[idx] = Double.parseDouble(data.trim());
			}
		};
	}
	
	public static ArrangeParsee<String> createStringArrange(){
		return new AbstractArrangeDefaultParsee<String>(){
			@Override
			public void parse(int idx, String data) {
				_datas[idx] = new String(data.trim());
			}
		}.init(String.class);
	}
	
	public static ArrangeParsee<Double> createFrameRateArrange(){
		return new AbstractArrangeDefaultParsee<Double>(){
			@Override
			public void parse(int idx, String data) {
				_datas[idx] = FrameElement.calcRps(Double.parseDouble(data.trim()));
			}
		}.init(Double.class);
	}
}
