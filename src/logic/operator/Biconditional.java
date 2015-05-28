package logic.operator;

import java.util.HashMap;

import logic.Literal;
import logic.Logic;
import exception.NotSolvableException;

/**
 * Bidirectional Equivalence or XNOR. A<=>B is true if A and B are true or if A and B are false.
 * 
 * @author Devon
 *
 */
public class Biconditional implements Logic
{
    private Logic one, two;

    /**
     * 
     * @param firstTerm
     *            String of the first term in the biconditional. <b>(A|(B=>C))</b> <=> ((D&E)&F)|G
     * @param secondTerm
     *            String of the second term in the biconditional. (A|(B=>C)) <=> <b>((D&E)&F)|G</b>
     */
    public Biconditional(String first, String second)
    {
        String firstTerm = this.trimOuterBrackets(first);
        String secondTerm = this.trimOuterBrackets(second);
        int neutralPos1 = this.findBracketNeutrality(firstTerm);
        int neutralPos2 = this.findBracketNeutrality(secondTerm);

        if(neutralPos1 != -1)
        {
            switch(firstTerm.charAt(neutralPos1))
            {
                case '&':
                    this.one = new Conjunction(firstTerm.substring(0, neutralPos1),
                            firstTerm.substring(neutralPos1 + 1));
                    break;
                case '|':
                    this.one = new Disjunction(firstTerm.substring(0, neutralPos1),
                            firstTerm.substring(neutralPos1 + 1));
                    break;
                case '<':
                    this.one = new Biconditional(firstTerm.substring(0, neutralPos1),
                            firstTerm.substring(neutralPos1 + 3));
                    break;
                case '=':
                    this.one = new Biconditional(firstTerm.substring(0, neutralPos1),
                            firstTerm.substring(neutralPos1 + 2));
                    break;
            }
        }
        else
        {
            if(!firstTerm.startsWith("~"))
            {
                this.one = new Literal(firstTerm);
            }
            else
            {
                this.one = new Negation(firstTerm.substring(1));
            }
        }

        if(neutralPos2 != -1)
        {
            switch(secondTerm.charAt(neutralPos2))
            {
                case '&':
                    this.two = new Conjunction(secondTerm.substring(0, neutralPos2),
                            secondTerm.substring(neutralPos2 + 1));
                    break;
                case '|':
                    this.two = new Disjunction(secondTerm.substring(0, neutralPos2),
                            secondTerm.substring(neutralPos2 + 1));
                    break;
                case '<':
                    this.two = new Biconditional(secondTerm.substring(0, neutralPos2),
                            secondTerm.substring(neutralPos2 + 3));
                    break;
                case '=':
                    this.two = new Biconditional(secondTerm.substring(0, neutralPos2),
                            secondTerm.substring(neutralPos2 + 2));
                    break;
            }
        }
        else
        {
            if(!secondTerm.startsWith("~"))
            {
                this.two = new Literal(secondTerm);
            }
            else
            {
                this.two = new Negation(secondTerm.substring(1));
            }
        }
    }

    /**
     * @return the first logical term
     */
    public Logic getOne()
    {
        return this.one;
    }

    /**
     * @return the second logical term
     */
    public Logic getTwo()
    {
        return this.two;
    }

    @Override
    public boolean evaluate() throws NotSolvableException
    {
        if(this.canSolve())
        {
            boolean value = !(this.one.evaluate() ^ this.two.evaluate());
            return value;
        }

        throw new NotSolvableException();
    }

    @Override
    public boolean canSolve()
    {
        return this.one.canSolve() && this.two.canSolve();
    }

    @Override
    public HashMap<String, Literal> setTerms(HashMap<String, Literal> terms)
    {
        HashMap<String, Literal> tempTerms = terms;
        if(this.one instanceof Literal)
        {
            Literal temp = (Literal) this.one;

            if(tempTerms.containsKey(temp.getName()))
            {
                try
                {
                    tempTerms.get(temp.getName()).setValue(temp.evaluate());
                }
                catch(NotSolvableException e)
                {
                }
            }
            else
            {
                tempTerms.put(temp.getName(), temp);
            }

            this.one = tempTerms.get(temp.getName());
        }
        else
        {
            tempTerms = this.one.setTerms(tempTerms);
        }

        if(this.two instanceof Literal)
        {
            Literal temp = (Literal) this.two;

            if(tempTerms.containsKey(temp.getName()))
            {
                try
                {
                    tempTerms.get(temp.getName()).setValue(temp.evaluate());
                }
                catch(NotSolvableException e)
                {
                }
            }
            else
            {
                tempTerms.put(temp.getName(), temp);
            }

            this.two = tempTerms.get(temp.getName());
        }
        else
        {
            tempTerms = this.two.setTerms(tempTerms);
        }

        return tempTerms;
    }
}
