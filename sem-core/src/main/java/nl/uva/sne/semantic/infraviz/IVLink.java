package nl.uva.sne.semantic.infraviz;

public class IVLink implements IVConcept{

	private final String name;
	private final String sourcenode;
	private final String sinknode;
	
	public IVLink(String name, String sourcenode, String sinknode) {
		if (name == null) throw new NullPointerException("name is null");
		if (sourcenode == null) throw new NullPointerException("sourcenode is null");
		if (sinknode == null) throw new NullPointerException("sinknode is null");
		
		this.name = name;
		this.sourcenode = sourcenode;
		this.sinknode = sinknode;
	}
	
	@getter
	public String getName() {
		return this.name;
	}
	
	@getter
	public String getSourcenode() {
		return this.sourcenode;
	}
	
	@getter
	public String getSinknode() {
		return this.sinknode;
	}
	
}
