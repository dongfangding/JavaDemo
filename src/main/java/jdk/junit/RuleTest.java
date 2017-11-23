package main.java.jdk.junit;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class RuleTest {
	@Rule
	private TemporaryFolder tempFolder = new TemporaryFolder();

	@Rule
	private ExpectedException exception = ExpectedException.none();
	
	 @Test
	  public void countsAssets() throws IOException {
	    File icon = tempFolder.newFile("icon.png");
	    File assets = tempFolder.newFolder("assets");
	    createAssets(assets, 3);

	 /*   DigitalAssetManager dam = new DigitalAssetManager(icon, assets);
	    assertEquals(3, dam.getAssetCount());*/
	  }
	 
	 private void createAssets(File assets, int numberOfAssets) throws IOException {
	    for (int index = 0; index < numberOfAssets; index++) {
	      File asset = new File(assets, String.format("asset-%d.mpg", index));
	      Assert.assertTrue("Asset couldn't be created.", asset.createNewFile());
	    }
	  }
}
