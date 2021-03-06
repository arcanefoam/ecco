package at.jku.isse.ecco.plugin.artifact;

import at.jku.isse.ecco.listener.WriteListener;

public interface ArtifactWriter<I, O> {

	public abstract String getPluginId();

	public abstract O[] write(O base, I input);

	public abstract O[] write(I input);

	public void addListener(WriteListener listener);

	public void removeListener(WriteListener listener);

}
