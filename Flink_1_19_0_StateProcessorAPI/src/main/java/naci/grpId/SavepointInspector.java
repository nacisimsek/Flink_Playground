package naci.grpId;

// Keep existing imports
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.runtime.checkpoint.Checkpoints;
import org.apache.flink.runtime.checkpoint.OperatorState;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState; // Keep for size calculation
import org.apache.flink.runtime.checkpoint.metadata.CheckpointMetadata;
import org.apache.flink.runtime.state.KeyedStateHandle;    // Keep for size calculation and kind detection
import org.apache.flink.runtime.state.OperatorStateHandle; // Keep for size calculation and kind detection

import java.io.IOException;
import java.util.Collection; // Keep Collection import
import java.util.HashSet; // To collect unique state names
// Removed List import as it's not needed for subtaskStates
import java.util.Set;     // To store state names and kinds
import java.util.stream.Collectors; // For formatting state names

public class SavepointInspector {

    private static final String METADATA_FILENAME = "_metadata";

    public static void main(String[] args) {
        // --- CONFIGURATION ---
        String savepointDirectoryPath =
                "file:///Volumes/D/TICKETS/Open/3716_BMW_OperatorUID_Savepoint_Issue/"
                        + "20250414-Simpler_job_test/savepoint/savepoint/"
                        + "_savepoint-ba6451-249084404ebb";
        // ---------------------

        System.out.println("Attempting to read savepoint metadata from: " + savepointDirectoryPath);

        Path savepointDir = new Path(savepointDirectoryPath);
        ClassLoader classLoader = SavepointInspector.class.getClassLoader();

        try {
            FileSystem fs = savepointDir.getFileSystem();
            Path metadataFilePath = new Path(savepointDir, METADATA_FILENAME);

            if (!fs.exists(metadataFilePath)) {
                throw new IOException("Metadata file does not exist at expected path: " + metadataFilePath);
            }

            System.out.println("Found metadata file path: " + metadataFilePath);

            try (FSDataInputStream inStream = fs.open(metadataFilePath);
                 DataInputViewStreamWrapper inView = new DataInputViewStreamWrapper(inStream)) {

                CheckpointMetadata checkpointMetadata = Checkpoints.loadCheckpointMetadata(
                        inView,
                        classLoader,
                        savepointDirectoryPath
                );

                System.out.println("\nSuccessfully loaded metadata for Savepoint/Checkpoint ID: " + checkpointMetadata.getCheckpointId());
                System.out.println("----- Operator States (Summary) -----");

                Collection<OperatorState> operatorStates = checkpointMetadata.getOperatorStates();

                if (operatorStates.isEmpty()) {
                    System.out.println("No operator states found in the metadata.");
                } else {
                    // Header for the summary table (optional)
                    System.out.printf("%-40s %15s %12s %16s %15s %-30s%n",
                            "Operator ID", "State Size (B)", "Parallelism", "Max Parallelism", "State Kind(s)", "State Names");
                    System.out.println(String.format("%0" + 140 + "d", 0).replace("0", "-")); // Separator line

                    for (OperatorState opState : operatorStates) {
                        long totalStateSize = 0;
                        int numSubtasksWithState = 0;
                        Set<String> stateNames = new HashSet<>();
                        Set<String> stateKinds = new HashSet<>();

                        // *** FIX: Change type from List to Collection ***
                        Collection<OperatorSubtaskState> subtaskStates = opState.getStates();

                        if (subtaskStates != null) { // Check if collection itself is null (unlikely but safe)
                            for (OperatorSubtaskState subtaskState : subtaskStates) { // Iterate over Collection
                                if (subtaskState == null) continue; // Skip if a specific subtask state is null

                                long currentSubtaskSize = 0;
                                boolean subtaskHasState = false;

                                // Check Keyed State
                                if (subtaskState.getManagedKeyedState() != null && !subtaskState.getManagedKeyedState().isEmpty() ||
                                        subtaskState.getRawKeyedState() != null && !subtaskState.getRawKeyedState().isEmpty()) {
                                    stateKinds.add("Keyed");
                                    subtaskHasState = true;
                                    if (subtaskState.getManagedKeyedState() != null) {
                                        for (KeyedStateHandle handle : subtaskState.getManagedKeyedState()) {
                                            if (handle != null) currentSubtaskSize += handle.getStateSize();
                                        }
                                    }
                                    if (subtaskState.getRawKeyedState() != null) {
                                        for (KeyedStateHandle handle : subtaskState.getRawKeyedState()) {
                                            if (handle != null) currentSubtaskSize += handle.getStateSize();
                                        }
                                    }
                                }

                                // Check Operator State
                                if (subtaskState.getManagedOperatorState() != null && !subtaskState.getManagedOperatorState().isEmpty() ||
                                        subtaskState.getRawOperatorState() != null && !subtaskState.getRawOperatorState().isEmpty()) {
                                    stateKinds.add("Operator");
                                    subtaskHasState = true;
                                    if (subtaskState.getManagedOperatorState() != null) {
                                        for (OperatorStateHandle handle : subtaskState.getManagedOperatorState()) {
                                            if (handle != null) {
                                                currentSubtaskSize += handle.getStateSize();
                                                if (handle.getStateNameToPartitionOffsets() != null) {
                                                    stateNames.addAll(handle.getStateNameToPartitionOffsets().keySet());
                                                }
                                            }
                                        }
                                    }
                                    if (subtaskState.getRawOperatorState() != null) {
                                        for (OperatorStateHandle handle : subtaskState.getRawOperatorState()) {
                                            if (handle != null) {
                                                currentSubtaskSize += handle.getStateSize();
                                                if (handle.getStateNameToPartitionOffsets() != null) {
                                                    stateNames.addAll(handle.getStateNameToPartitionOffsets().keySet());
                                                }
                                            }
                                        }
                                    }
                                }

                                totalStateSize += currentSubtaskSize;
                                if (subtaskHasState) {
                                    numSubtasksWithState++;
                                }
                            }
                        }

                        // Format collected info for printing
                        String stateKindsStr = stateKinds.isEmpty() ? "Stateless" : String.join(",", stateKinds);
                        String stateNamesStr = stateNames.isEmpty() ? "-" : stateNames.stream().collect(Collectors.joining(",", "[", "]"));
                        int reportedSubtasks = opState.getParallelism();

                        System.out.printf("%-40s %15d %12d %16d %15s %-30s%n",
                                opState.getOperatorID(),
                                totalStateSize,
                                opState.getParallelism(),
                                opState.getMaxParallelism(),
                                stateKindsStr,
                                stateNamesStr
                        );
                    }
                }
                System.out.println("---------------------------------------");

            } // Stream and view are auto-closed here

        } catch (IOException e) {
            System.err.println("Error reading savepoint metadata: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}