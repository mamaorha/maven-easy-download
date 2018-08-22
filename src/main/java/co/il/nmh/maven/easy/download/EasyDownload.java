package co.il.nmh.maven.easy.download;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import co.il.nmh.easy.utils.FileUtils;
import co.il.nmh.easy.utils.rest.EasyRestClient;
import co.il.nmh.easy.utils.rest.data.EasyRestHeader;
import co.il.nmh.easy.utils.rest.data.RestClientResponse;
import co.il.nmh.maven.easy.download.data.Header;

/**
 * @author Maor Hamami
 */
@Mojo(name = "easy-download", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = false)
public class EasyDownload extends AbstractMojo
{
	@Parameter(property = "download.url", required = true)
	private String url;

	@Parameter(property = "download.method", required = false, defaultValue = "GET")
	private String method;

	@Parameter(property = "download.headers", required = false)
	private List<Header> headers;

	@Parameter(property = "download.payload", required = false)
	private String payload;

	@Parameter(property = "download.outputFileName", required = true)
	private String outputFileName;

	@Parameter(property = "download.outputDirectory", required = true)
	private String outputDirectory;

	@Parameter(property = "download.overwrite", required = false, defaultValue = "false")
	private Boolean overwrite;

	public void execute() throws MojoExecutionException, MojoFailureException
	{
		File dir = new File(outputDirectory);

		if (dir.exists() && !dir.isDirectory())
		{
			throw new MojoExecutionException(String.format("invalid output directory [%s]", outputDirectory));
		}

		if (!FileUtils.createDir(outputDirectory))
		{
			throw new MojoExecutionException("Failed to create dir at " + dir.getPath());
		}

		File file = new File(outputDirectory + File.separator + outputFileName);

		if (file.exists() && !overwrite)
		{
			getLog().info(String.format("file [%s] already exist and overite set to false, ignoring", file.getPath()));
		}
		else
		{
			getLog().info(String.format("trying to download file [%s]", url));

			try
			{
				EasyRestHeader easyRestHeader = new EasyRestHeader();

				for (Header header : headers)
				{
					String value = header.getValue();

					if (value.startsWith(" "))
					{
						value = value.substring(1);
					}

					easyRestHeader.addHeader(header.getName(), value);
				}

				byte[] payloadBytes = null;

				if (null != payload)
				{
					payloadBytes = payload.getBytes();
				}

				RestClientResponse restClientResponse = EasyRestClient.execute(url, method, easyRestHeader, payloadBytes);

				if (restClientResponse.getHttpStatus() >= 300)
				{
					throw new MojoExecutionException("download failed, http status: " + restClientResponse.getHttpStatus());
				}

				Files.write(file.toPath(), restClientResponse.getResponse());

				getLog().info(String.format("download complete successfuly - [%s]", file.getPath()));
			}
			catch (Exception e)
			{
				if (e instanceof MojoExecutionException)
				{
					throw (MojoExecutionException) e;
				}

				throw new MojoExecutionException(e.getMessage(), e);
			}
		}
	}
}
