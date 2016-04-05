package at.jku.isse.ecco.web.rest;

import at.jku.isse.ecco.EccoService;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.nio.file.Paths;

@ApplicationPath("ecco")
public class EccoApplication extends ResourceConfig {

	private EccoService eccoService = new EccoService();

	public EccoApplication() {
		packages("at.jku.isse.ecco.web.rest");

		property("eccoService", this.eccoService);
	}

	public EccoService getEccoService() {
		return this.eccoService;
	}

	public void init(String repositoryDir) {
		this.eccoService.setRepositoryDir(Paths.get(repositoryDir));
		this.eccoService.init();
	}

	public void destroy() {
		this.eccoService.destroy();
	}

}