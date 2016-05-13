package nl.uva.sne.semantic.infraviz;

public class IVNode implements IVConcept {

	private final String name;
	
	public IVNode(String name) {
		if (name == null) throw new NullPointerException("name is null");
	
		this.name = name;
	}
	
	@getter
	public String getName() {
		return this.name;
	}
	
	
}
