package at.jku.isse.ecco.plugin.artifact;

import at.jku.isse.ecco.tree.Node;

public interface ArtifactViewer {

	public abstract String getPluginId();

	public void showTree(Node node);

}
