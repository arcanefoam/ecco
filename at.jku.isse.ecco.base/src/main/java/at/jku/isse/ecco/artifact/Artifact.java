package at.jku.isse.ecco.artifact;

import at.jku.isse.ecco.sg.SequenceGraph;
import at.jku.isse.ecco.tree.Node;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Public interface for artifacts that stores the actual data and references to other artifacts.
 *
 * @param <DataType> The type of the data object stored in the artifact.
 */
public interface Artifact<DataType extends ArtifactData> {

	/**
	 * Setting this property indicates that the artifact's file representation was not modified since it was written.
	 */
	public static final String PROPERTY_UNMODIFIED = "unmodified";

	/**
	 * This property is set by some tree operations (see for example {@link at.jku.isse.ecco.util.Trees#slice(Node.Op, Node.Op)} and then subsequently used by other tree operations (see for example {@link at.jku.isse.ecco.util.Trees#updateArtifactReferences(Node.Op)}.
	 */
	public static final String PROPERTY_REPLACING_ARTIFACT = "replacingArtifact";

	/**
	 * Artifacts can be marked by setting this property. Other operations can then use this property (see for example {@link at.jku.isse.ecco.util.Trees#extractMarked(Node.Op)}).
	 */
	public static final String PROPERTY_MARKED_FOR_EXTRACTION = "marked";

	/**
	 * This property is set by {@link at.jku.isse.ecco.util.Trees#map(Node.Op, Node.Op)} and contains another artifact from another tree that maps to this one.
	 */
	public static final String PROPERTY_MAPPED_ARTIFACT = "mapped";

	/**
	 * A constant indicating that an artifact does not yet have a sequence number assigned.
	 */
	public static final int UNASSIGNED_SEQUENCE_NUMBER = -1;


	/**
	 * An artifact's hash code is based on the hash code if the contained data object and on its type (i.e. ordered or unordered).
	 * An artifact must NOT use its sequence number for the computation of its hash code!
	 *
	 * @return The hash code of this artifact.
	 */
	@Override
	public int hashCode();

	/**
	 * Two artifacts are equal when
	 * 1) the contained data objects are equal,
	 * 2) they are of the same type (i.e. both ordered or both unordered), and
	 * 3) when both have a sequence number assigned, these sequence numbers must be equal.
	 *
	 * @param obj The object to compare to this artifact.
	 * @return True if this artifact is equal to the given object, false otherwise.
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * The string representation of an artifact is determined by the contained data object.
	 *
	 * @return The string representation of the artifact.
	 */
	@Override
	public String toString();


	/**
	 * The data object stored in the artifact. The type and its contents are determined by the artifat plugin that created the artifact.
	 *
	 * @return The data object that was stored in the artifact at the time it was created by an artifact plugin.
	 */
	public DataType getData();


	/**
	 * An artifact that is atomic will not be split up further, even if it has children.
	 * In other words, the children of an atomic artifact (or more accurately, the children of nodes containing an atomic artifact) will not be split up by any tree operation (see for example {@link at.jku.isse.ecco.util.Trees#slice(Node.Op, Node.Op)}).
	 *
	 * @return True if the artifact is atomic, false otherwise.
	 */
	public boolean isAtomic();

	/**
	 * There are two types of artifacts: ordered and unordered.
	 * <p>
	 * <i>Unordered</i> artifacts do not contain a sequence graph (see {@link at.jku.isse.ecco.sg.SequenceGraph}) and therefore its children (or more accurately the children of nodes containing the unordered artifact) will not be assigned a sequence number.
	 * As a consequence of this, and because the children of an artifact must be unique, the child artifacts of an unordered artifact must be uniquely identifiable just by their contained data object, as it is the only means of identification aside from their sequence number (see {@link at.jku.isse.ecco.artifact.Artifact#equals(Object)}.
	 * In other words, no two child artifacts can contain equal data objects.
	 * <p>
	 * <i>Ordered</i> artifacts are assigned a sequence graph (see {@link at.jku.isse.ecco.sg.SequenceGraph}) the first time they are being processed by any operation (see for example {@link at.jku.isse.ecco.util.Trees#slice(Node.Op, Node.Op)}).
	 * This process is called <i>sequencing</i> of an ordered artifact. During this process the children of the ordered artifact are assigned sequence numbers based on their order of occurrence.
	 * This assigned sequence number is used as an additional means of identifying the child artifacts. This makes it possible to have child artifacts containing equal data objects but different sequence numbers.
	 * This is for example necessary when the child artifacts represent statements in a programming language: statements are not unique, the same statement can appear multiple times in a sequence of statements, and the position of a statement in the sequence matters. This is what the sequence number is used for.
	 *
	 * @return
	 */
	public boolean isOrdered();

	/**
	 * Determines whether the artifact contains a sequence graph.
	 * An unordered node will never be sequenced (i.e. never contain a sequence graph).
	 * An ordered node will initially (after its creation) also not be sequenced. After being processed for the first time by any operation (see for example {@link at.jku.isse.ecco.util.Trees#slice(Node.Op, Node.Op)}) it will be assigned a sequence graph.
	 *
	 * @return True when the artifact has a sequence graph assigned, false otherwise
	 */
	public boolean isSequenced();

	public SequenceGraph getSequenceGraph();

	/**
	 * Returns the assigned sequence number in case this artifact is the child of an ordered artifact that has already been sequenced, or {@link at.jku.isse.ecco.artifact.Artifact#UNASSIGNED_SEQUENCE_NUMBER} otherwise.
	 *
	 * @return The assigned sequence number in case this artifact is the child of an ordered artifact that has already been sequenced, or {@link at.jku.isse.ecco.artifact.Artifact#UNASSIGNED_SEQUENCE_NUMBER} otherwise.
	 */
	public int getSequenceNumber();

	/**
	 * Returns the one unique node from the artifact tree that contains this artifact.
	 *
	 * @return The containing node.
	 */
	public Node getContainingNode();


	/**
	 * Sets the containing node from the artifact tree.
	 *
	 * @param node that this artifact contains (@Nullable)
	 */
	public void setContainingNode(Node.Op node);


	// REFERENCES

	/**
	 * Returns the references to the artifacts that this artifact uses. A uses reference describes which other artifact this artifact depends on.
	 *
	 * @return References to the used artifacts.
	 */
	public Collection<? extends ArtifactReference> getUses();

	/**
	 * Returns the references to the artifacts that are used by this artifact. A used by reference describes which other artifact uses this artifact..
	 *
	 * @return References to artifacts this artifact is used by.
	 */
	public Collection<? extends ArtifactReference> getUsedBy();


	// PROPERTIES

	/**
	 * Returns the property with the given name in form of an optional. The optional will only contain a result if the name and the type are correct. It is not possible to store different types with the same name as the name is the main criterion. Thus using the same name overrides old properties.
	 * <p>
	 * These properties are volatile, i.e. they are not persisted!
	 *
	 * @param name of the property that should be retrieved
	 * @return An optional which contains the actual property or nothing.
	 */
	public <T> Optional<T> getProperty(String name);

	/**
	 * Adds a new property to this artifact. It is not possible to store different types with the same name as the name is the main criterion. Thus using the same name overrides old properties.
	 * <p>
	 * These properties are volatile, i.e. they are not persisted!
	 *
	 * @param property that should be added
	 */
	public <T> void putProperty(String name, T property);

	/**
	 * Removes the property with the given name. If the name could not be found in the map it does nothing.
	 *
	 * @param name of the property that should be removed
	 */
	public void removeProperty(String name);


	// OPERATION INTERFACE

	/**
	 * Private artifact operand interface used internally and not passed outside.
	 *
	 * @param <DataType> The type of the data object stored in the artifact.
	 */
	public interface Op<DataType extends ArtifactData> extends Artifact<DataType> {

		/**
		 * Sets whether this artifact is atomic or not (see {@link Artifact#isAtomic()}).
		 *
		 * @param atomic Whether the artifact is atomic (true) or not (false).
		 */
		public void setAtomic(boolean atomic);

		/**
		 * Sets whether this artifact is ordered or not (see {@link Artifact#isOrdered()}).
		 *
		 * @param ordered Whether the artifact is ordered (true) or not (false).
		 */
		public void setOrdered(boolean ordered);

		/**
		 * Sets the sequence graph of this artifact.
		 *
		 * @param sequenceGraph The sequence graph.
		 */
		public void setSequenceGraph(SequenceGraph.Op sequenceGraph);

		/**
		 * Sets the sequence number of the artifact. This is used by the sequence graph.
		 *
		 * @param sequenceNumber
		 */
		public void setSequenceNumber(int sequenceNumber);


		// TODO: document these! make clear where a check is performed for "already existing" or "null" etc.

		public void checkConsistency();

		public boolean hasReplacingArtifact();

		public Op getReplacingArtifact();

		public void setReplacingArtifact(Op replacingArtifact);

		public void updateArtifactReferences();

		public boolean uses(Op target);

		public void addUses(Op artifact);

		public void addUses(Op artifact, String type);


		public SequenceGraph.Op getSequenceGraph();


		/**
		 * Adds a used by reference to the artifact. A used by association describes from which other artifact this artifact is used in some manner.
		 * This should be interface private. Do not use it outside of classes that implement this interface.
		 *
		 * @param reference to the artifact that uses this artifact
		 */
		public void addUsedBy(ArtifactReference.Op reference);

		/**
		 * Adds a uses reference to the artifact. A uses by association describes which other artifact this artifact uses in some manner.
		 * This should be interface private. Do not use it outside of classes that implement this interface.
		 *
		 * @param reference to the artifact that uses this artifact
		 */
		public void addUses(ArtifactReference.Op reference);

		@Override
		public Collection<ArtifactReference.Op> getUses();

		@Override
		public Collection<ArtifactReference.Op> getUsedBy();

		public Map<String, Object> getProperties();

		@Override
		public Node.Op getContainingNode();


		public SequenceGraph.Op createSequenceGraph();

		// TODO: possibly remove these:

		public boolean useReferencesInEquals();

		public void setUseReferencesInEquals(boolean useReferenesInEquals);
	}

}
