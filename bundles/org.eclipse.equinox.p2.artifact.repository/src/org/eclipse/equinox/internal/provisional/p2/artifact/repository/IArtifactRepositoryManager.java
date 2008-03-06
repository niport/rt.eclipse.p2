/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.provisional.p2.artifact.repository;

import java.net.URL;
import java.util.Properties;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.core.repository.IRepository;
import org.eclipse.equinox.internal.provisional.p2.metadata.IArtifactKey;

public interface IArtifactRepositoryManager {
	public static final IArtifactRequest[] NO_ARTIFACT_REQUEST = new IArtifactRequest[0];

	/**
	 * Constant used to indicate that all repositories are of interest.
	 * @see #getKnownRepositories(int)
	 */
	public static final int REPOSITORIES_ALL = 0;

	/**
	 * Constant used to indicate that system repositories are of interest.
	 * @see IRepository#PROP_SYSTEM
	 * @see #getKnownRepositories(int)
	 */
	public static final int REPOSITORIES_SYSTEM = 1 << 0;

	/**
	 * Constant used to indicate that non-system repositories are of interest
	 * @see IRepository#PROP_SYSTEM
	 * @see #getKnownRepositories(int)
	 */
	public static final int REPOSITORIES_NON_SYSTEM = 1 << 1;

	/**
	 * Constant used to indicate that local repositories are of interest.
	 * @see #getKnownRepositories(int)
	 */
	public static final int REPOSITORIES_LOCAL = 1 << 2;

	/**
	 * Repository type for a simple repository based on a URL or local file system location.
	 */
	public static final String TYPE_SIMPLE_REPOSITORY = "org.eclipse.equinox.p2.artifact.repository.simpleRepository"; //$NON-NLS-1$

	/**
	 * Adds a repository to the list of artifact repositories tracked by the repository
	 * manager.
	 * 
	 * @param location The location of the artifact repository to add
	 */
	public void addRepository(URL location);

	/**
	 * Return a new request to download the given artifact and store it at the given destination.
	 * @param key the artifact to download
	 * @param destination the destination where the artifact will be stored
	 * @return the newly created request object
	 */
	public IArtifactRequest createDownloadRequest(IArtifactKey key, IPath destination);

	/**
	 * Return a new request to mirror the given artifact into the destination repository.
	 * @param key the artifact to mirror
	 * @param destination the destination where the artifact will be mirrored
	 * @return the newly created request object
	 */
	public IArtifactRequest createMirrorRequest(IArtifactKey key, IArtifactRepository destination);

	/**
	 * Return a new request to mirror the given artifact into the destination repository.
	 * @param key the artifact to mirror
	 * @param destination the destination where the artifact will be mirrored
	 * @param destinationDescriptorProperties additional properties for use in creating the repositor's ArtifactDescriptor
	 * @param destinationRepositoryProperties additional repository specific properties for use in creating the repositor's ArtifactDescriptor
	 * @return the newly created request object
	 */
	public IArtifactRequest createMirrorRequest(IArtifactKey key, IArtifactRepository destination, Properties destinationDescriptorProperties, Properties destinationRepositoryProperties);

	/**
	 * Creates and returns a new empty artifact repository of the given type at 
	 * the given location.
	 * <p>
	 * The resulting repository is <b>not</b> added to the list of repositories tracked by
	 * the repository manager. Clients must make a subsequent call to {@link #addRepository(URL)}
	 * if they want the repository manager to remember the repository for subsequent
	 * load attempts.
	 * </p>
	 * 
	 * @param location the location for the new repository
	 * @param name the name of the new repository
	 * @param type the kind of repository to create
	 * @return the newly created repository
	 * @throws ProvisionException if the repository could not be created.  Reasons include:
	 * <ul>
	 * <li>The repository type is unknown.</li>
	 * <li>There was an error writing to the given repository location.</li>
	 * <li>A repository already exists at that location.</li>
	 * </ul>
	 */
	public IArtifactRepository createRepository(URL location, String name, String type) throws ProvisionException;

	/**
	 * Returns the artifact repository locations known to the repository manager.
	 * <p>
	 * Note that the repository manager does not guarantee that a valid repository
	 * exists at any of the returned locations at any particular moment in time.
	 * A subsequent attempt to load a repository at any of the given locations may
	 * or may not succeed.
	 * 
	 * @param flags an integer bit-mask indicating which repositories should be
	 * returned.  <code>REPOSITORIES_ALL</code> can be used as the mask when
	 * all repositories should be returned.
	 * 
	 * @return the locations of the repositories managed by this repository manager.
	 * 
	 * @see #REPOSITORIES_ALL
	 * @see #REPOSITORIES_SYSTEM
	 * @see #REPOSITORIES_NON_SYSTEM
	 * @see #REPOSITORIES_LOCAL
	 */
	public URL[] getKnownRepositories(int flags);

	/**
	 * Returns the property associated with the repository at the given URL, 
	 * without loading the repository.
	 * <p>
	 * Note that some properties for a repository can only be
	 * determined when that repository is loaded.  This method will return <code>null</code>
	 * for such properties.  Only values for the properties that are already
	 * known by a repository manager will be returned. 
	 * <p>
	 * If a client wishes to retrieve a property value from a repository 
	 * regardless of the cost of retrieving it, the client should load the 
	 * repository and then retrieve the property from the repository itself.
	 * 
	 * @param location the URL of the repository in question
	 * @param key the String key of the property desired
	 * @return the value of the property, or <code>null</code> if the repository
	 * does not exist, the value does not exist, or the property value 
	 * could not be determined without loading the repository.
	 * 
	 * @see #loadRepository(URL, IProgressMonitor)
	 * @see IRepository#getProperties()
	 * 
	 */
	public String getRepositoryProperty(URL location, String key);

	/**
	 * Loads the repository at the given location.  The location is expected to contain 
	 * data that describes a valid artifact repository of a known type.  If this manager
	 * already knows a repository at the given location then that repository is returned.
	 * <p>
	 * The resulting repository is added to the list of repositories tracked by
	 * the repository manager. Clients must make a subsequent call to {@link #removeRepository(URL)}
	 * if they do not want the repository manager to remember the repository for subsequent
	 * load attempts.
	 * </p>
	 * 
	 * @param location the location in which to look for a repository description
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting is not desired
	 * @return a repository object for the given location
	 * @throws ProvisionException if the repository could not be created.  Reasons include:
	 * <ul>
	 * <li>There is no existing repository at that location.</li>
	 * <li>The repository at that location could not be read.</li>
	 * </ul>
	 */
	public IArtifactRepository loadRepository(URL location, IProgressMonitor monitor) throws ProvisionException;

	/**
	 * Remove the given repository from this manager.  Do nothing if the repository
	 * is not currently managed.
	 * 
	 * @param location the location of the repository to remove
	 * @return <code>true</code> if a repository was removed, and 
	 * <code>false</code> otherwise.
	 */
	public boolean removeRepository(URL location);
}