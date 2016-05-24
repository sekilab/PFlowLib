package jp.ac.ut.csis.pflow.routing2.loader;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.ac.ut.csis.pflow.routing2.res.Link;
import jp.ac.ut.csis.pflow.routing2.res.Network;
import jp.ac.ut.csis.pflow.routing2.res.Node;

/**
 * Class for network data loader for CSV
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public abstract class ACsvNetworkLoader extends ANetworkLoader {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** Logger */
	private static final Logger LOGGER = LogManager.getLogger(ACsvNetworkLoader.class);
	
	
	/**
	 * delimiter type
	 */
	public enum Delimiter {
		/** comma 	*/	CSV,
		/** tab		*/	TSV
		;
	}

	
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** network file	*/	private File	  _networkFile;	
	/** flag for header	*/	private boolean   _hasHeader;
	/** delimiter type	*/	private Delimiter _delimiter;


	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create instance from network data with CSV header
	 * @param networkfile data file
	 */
	public ACsvNetworkLoader(File networkfile) { 
		this(networkfile,true);
	}
	
	/**
	 * create instance from network data with CSV
	 * @param networkfile data file 
	 * @param hasHeader flag for header existence
	 */
	public ACsvNetworkLoader(File networkfile,boolean hasHeader) { 
		this(networkfile,hasHeader,Delimiter.CSV);
	}
	
	/**
	 * create instance from network data with header
	 * @param networkfile data file 
	 * @param delimType delimiter type {@link Delimiter}
	 */
	public ACsvNetworkLoader(File networkfile,Delimiter delimType) { 
		this(networkfile,true,delimType);
	}	
	
	/**
	 * create instance from network data
	 * @param networkfile data file
	 * @param hasHeader flag for header existence
	 * @param delimType delimiter type {@link Delimiter}
	 */
	public ACsvNetworkLoader(File networkfile,boolean hasHeader,Delimiter delimType) { 
		super();
		
		// initialization
		_networkFile = networkfile;
		_delimiter   = delimType;
		_hasHeader   = hasHeader;
	}
	

	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * parse data line to a link
	 * @param network  network instance
	 * @param line link data record
	 * @return link instance
	 */
	protected abstract Link parseLine(Network network,String line);
	
	/**
	 * get delimiter
	 * @return delimiter 
	 * @see Delimiter
	 */
	protected Delimiter getDelimiter() {
		return _delimiter;
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.loader.QueryCondition[]) */
	@Override
	public Network load(QueryCondition[] qcs) {
		return load(new Network(),qcs);
	}

	/**
	 * load network data within the indicated areas
	 * @param network network instance to add data 
	 * @param qcs query conditions
	 * @return network
	 */
	public Network load(Network network,QueryCondition[] qcs) {
		String line = null;
		try(BufferedReader br = new BufferedReader(new FileReader(_networkFile))) {
			// check header existence =====================
			if( _hasHeader ) { 
				line = br.readLine(); 
			}

			while ( (line=br.readLine())!=null ) {
				Link link = parseLine(network,line);

				// case: has condition
				if(qcs != null && qcs.length > 0) {
					if( validate(qcs,link)) { 
						network.addLink(link); 
					}
				}
				// case: no condition
				else {
					network.addLink(link);
				}
			}
		}
		catch(IOException exp) { 
			LOGGER.error("fail to load network",exp); 
		}
		finally { 
			System.gc(); 
		}
		
		return network;
	}
	
	/**
	 * link data validation
	 * @param qcs query conditions
	 * @param link link to validate
	 * @return result
	 */
	protected boolean validate(QueryCondition[] qcs,Link link) {
		Node head = link.getHeadNode();
		Node tail = link.getTailNode();
		for(QueryCondition qc:qcs) { 
			Rectangle2D bounds = qc.getBounds();
			if (bounds.contains(head.getLon(),head.getLat()) || 
				bounds.contains(tail.getLon(),tail.getLat())) 
			{
				return true;
			}
		}
		return false;
	}
}
