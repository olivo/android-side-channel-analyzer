import java.io.File;
import java.util.ArrayList;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.jimple.infoflow.android.source.AndroidSourceSinkManager;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.entryPointCreators.AndroidEntryPointCreator;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.source.DefaultSourceSinkManager;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.options.Options;
import soot.Scene;

class SideChannelAnalyzer {

    public static void main(String[] args) throws Exception{

	System.out.println("=== Starting Side Channel Analyzer ===");

	// Configuration information.
	String androidJar = "soot/platforms";
	String apkFileLocation = "benchmarks/com.facebook.katana.apk";
	boolean forceAndroidJar = false;

	if(args.length > 0) {
	    apkFileLocation = args[0];
	}

	System.out.println("Analyzing APK: " + apkFileLocation);
	SetupApplication app = new SetupApplication(androidJar, apkFileLocation);
	EasyTaintWrapper easyTaintWrapper = new EasyTaintWrapper(new File("EasyTaintWrapperSource.txt"));
	app.setTaintWrapper(easyTaintWrapper);

	InfoflowAndroidConfiguration config = app.getConfig();


	config.setAccessPathLength(3);
	config.setComputeResultPaths(false);
	config.setEnableCallbacks(false);
	config.setEnableCallbackSources(false);
	config.setEnableArraySizeTainting(true);
	config.setEnableExceptionTracking(false);
	config.setEnableImplicitFlows(false);
	config.setEnableStaticFieldTracking(true);
	config.setEnableTypeChecking(true);
	// Ignoring system packages can improve scalability.
	config.setIgnoreFlowsInSystemPackages(false);
	config.setInspectSources(false);
	config.setInspectSinks(false);
	config.setFlowSensitiveAliasing(true);
	config.setLayoutMatchingMode(AndroidSourceSinkManager.LayoutMatchingMode.NoMatch);
	config.setMergeNeighbors(false);
	config.setOneResultPerAccessPath(false);
	config.setPathAgnosticResults(true);
	// Stopping after one result is also good for scalability.
	config.setStopAfterFirstFlow(false);
	config.setUseRecursiveAccessPaths(true);
	config.setUseThisChainReduction(true);
	config.setUseTypeTightening(true);

	app.setConfig(config);
	

	app.calculateSourcesSinksEntrypoints("SourcesAndSinks.txt");
	app.runInfoflow();
	
	System.out.println("=== Finishing Side Channel Analyzer ===");
    }
}
