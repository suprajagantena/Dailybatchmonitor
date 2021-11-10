package DLCR;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackStatus;

public class StartDLCR {
	
	@SuppressWarnings("deprecation")
	public String createStack() throws Exception
	{
//    	System.getProperties().put("https.proxySet", "true");
//    	System.getProperties().put("https.proxyHost", "proxy.kdc.capitalone.com");
//    	System.getProperties().put("https.proxyPort", "8099");

		 String stackName="startDLCR";
		 CreateStackRequest CF=new CreateStackRequest();
		 CF.setTemplateURL("s3UrlforCFT");
		 CF.setStackName(stackName);
		 AWSCredentials awsCreds = new BasicAWSCredentials("accesskey","secretkey");
//		 AmazonCloudFormationClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
	     AmazonCloudFormation stackbuilder = new AmazonCloudFormationClient(awsCreds);
	     Region usWest2 = Region.getRegion(Regions.US_EAST_1);
	     stackbuilder.setRegion(usWest2);
		 stackbuilder.createStack(CF);
		 
         // Wait for stack to be created
         // Note that you could use SNS notifications on the CreateStack call to track the progress of the stack creation
         //return "Stack creation completed, the stack " + stackName + " completed with " + waitForCompletion(stackbuilder, stackName);
		 return "Stack creation completed and DLCR is in progress";
	}

    // Wait for a stack to complete transitioning
    // End stack states are:
    //    CREATE_COMPLETE
    //    CREATE_FAILED
    //    DELETE_FAILED
    //    ROLLBACK_FAILED
    // OR the stack no longer exists
    public static String waitForCompletion(AmazonCloudFormation stackbuilder, String stackName) throws Exception {

        DescribeStacksRequest wait = new DescribeStacksRequest();
        wait.setStackName(stackName);
        Boolean completed = false;
        String  stackStatus = "Unknown";
        String  stackReason = "";

        System.out.print("Waiting");

        while (!completed) {
            List<Stack> stacks = stackbuilder.describeStacks(wait).getStacks();
            if (stacks.isEmpty())
            {
                completed   = true;
                stackStatus = "NO_SUCH_STACK";
                stackReason = "Stack has been deleted";
            } else {
                for (Stack stack : stacks) {
                    if (stack.getStackStatus().equals(StackStatus.CREATE_COMPLETE.toString()) ||
                            stack.getStackStatus().equals(StackStatus.CREATE_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.ROLLBACK_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.DELETE_FAILED.toString())) {
                        completed = true;
                        stackStatus = stack.getStackStatus();
                        stackReason = stack.getStackStatusReason();
                    }
                }
            }

            // Show we are waiting
            System.out.print(".");

            // Not done yet so sleep for 10 seconds.
            if (!completed) Thread.sleep(10000);
        }

        // Show we are done
        System.out.print("done\n");

        return stackStatus + " (" + stackReason + ")";
    }
	
}
