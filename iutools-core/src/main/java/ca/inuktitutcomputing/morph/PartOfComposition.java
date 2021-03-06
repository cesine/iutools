//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//
// -----------------------------------------------------------------------
//           (c) Conseil national de recherches Canada, 2003
//           (c) National Research Council of Canada, 2003
// -----------------------------------------------------------------------

// -----------------------------------------------------------------------
// Document/File:		Morceau.java
//
// Type/File type:		code Java / Java code
// 
// Auteur/Author:		Benoit Farley
//
// Organisation/Organization:	Conseil national de recherches du Canada/
//				National Research Council Canada
//
// Date de cr�ation/Date of creation:	
//
// Description: Classe sup�rieure englobant les morceaux-racine et
//              les morceaux-affixe.
//
// -----------------------------------------------------------------------

package ca.inuktitutcomputing.morph;

import ca.inuktitutcomputing.data.LinguisticDataException;
import ca.inuktitutcomputing.data.Morpheme;

// Morceau:	String terme
// 			int    position
// 			String niveau
//
// MorceauRacine:	Base racine
//
// MorceauAffixe:	SurfaceFormOfAffix      form
//				   	boolean            reflexif
//             		TerminaisonVerbale tv

public abstract class PartOfComposition implements Cloneable {

    String term;
    int position;
    Graph.Arc arc;
    Graph.Arc arcs[];


    public void setTerme(String term) {
    	this.term = term;
    }


    public String getTerm() {
    	return term;
    }
    
    public int getPosition() {
    	return position;
    }
    
    public Graph.Arc[] getArcs() {
    	return arcs;
    }
    
    public void setArcs(Graph.Arc _arcs[]) {
    	arcs = _arcs;
    }
    
    public Graph.Arc getArc() {
    	return arc;
    }


    public PartOfComposition copyOf() throws CloneNotSupportedException {
    	return (PartOfComposition)this.clone();
    }


    
    abstract Morpheme getMorpheme() throws LinguisticDataException;
    
    
	 
}
