package logic;

import java.util.HashMap;
import java.util.LinkedList;

import logic.operator.Biconditional;
import logic.operator.Conditional;
import logic.operator.Conjunction;
import logic.operator.Disjunction;
import logic.operator.Negation;
import logic.operator.Operator;
import exception.NotSolvableException;

public class Sentence implements Logic
{
    Logic premise, conclusion;
    LinkedList<Operator> operators = new LinkedList<Operator>();
    Literal literal;
    String sentance;

    /**
     * 
     * @param sentence
     *            string containing no spaces of a clause (many terms)
     */
    public Sentence(String sentence)
    {
        this.sentance = sentence;

        int neutralPos1 = this.findBracketNeutrality(this.trimOuterBrackets(sentence));

        if(neutralPos1 != -1)
        {
            Operator o = null;
            switch(sentence.charAt(neutralPos1))
            {
                case '&':
                    o = new Conjunction(sentence.substring(0, neutralPos1), sentence.substring(neutralPos1 + 1));
                    this.operators.add(o);
                    break;
                case '|':
                    o = new Disjunction(sentence.substring(0, neutralPos1), sentence.substring(neutralPos1 + 1));
                    this.operators.add(o);
                    break;
                case '<':
                    o = new Biconditional(sentence.substring(0, neutralPos1), sentence.substring(neutralPos1 + 3));
                    this.operators.add(o);
                    break;
                case '=':
                    o = new Conditional(sentence.substring(0, neutralPos1), sentence.substring(neutralPos1 + 2));
                    this.operators.add(o);
                    break;
            }

            this.premise = o.getOne();
            this.conclusion = o.getTwo();
        }
        else
        {
            Literal t = null;
            if(!sentence.startsWith("~"))
            {
                t = new Literal(sentence);
                t.setValue(true);
            }
            else
            {
                Operator not = new Negation(sentence.substring(1));
                if(not.getOne() instanceof Literal)
                {
                    t = (Literal) not.getOne();
                    t.setValue(false);
                }
                else
                {
                    this.operators.add(not);
                }
            }

            this.literal = t;
        }
    }

    public String getSentence()
    {
        return this.sentance;
    }

    public LinkedList<Literal> getPremise()
    {
        return this.premise.getLiterals();
    }

    public LinkedList<Literal> getConclusion()
    {
        return this.conclusion.getLiterals();
    }

    @Override
    public LinkedList<Literal> getLiterals()
    {
        LinkedList<Literal> allLogic = new LinkedList<Literal>();
        for(Logic l : this.operators)
        {
            allLogic.addAll(l.getLiterals());
        }

        allLogic.add(this.literal);

        return allLogic;
    }

    @Override
    public boolean evaluate() throws NotSolvableException
    {
        boolean value = true;
        if(this.literal != null)
        {
            value &= this.literal.evaluate();
        }

        for(Logic l : this.operators)
        {
            value &= l.evaluate();
        }

        return value;
    }

    @Override
    public boolean canSolve()
    {
        if(!this.literal.canSolve())
        {
            return false;
        }

        for(Logic l : this.operators)
        {
            if(!l.canSolve())
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public HashMap<String, Literal> setTerms(HashMap<String, Literal> terms)
    {
        HashMap<String, Literal> tempTerms = terms;

        if(this.literal != null)
        {
            if(terms.containsKey(this.literal.getName()))
            {
                Literal temp = tempTerms.get(this.literal.getName());
                if(!temp.canSolve())
                {
                    try
                    {
                        temp.setValue(this.literal.evaluate());
                    }
                    catch(NotSolvableException e)
                    {
                    }
                }

                this.literal = temp;
            }
            else
            {
                tempTerms.put(this.literal.getName(), this.literal);
            }
        }

        for(Logic op : this.operators)
        {
            tempTerms.putAll(op.setTerms(tempTerms));

        }

        if(!(this.premise instanceof Literal) && !(this.conclusion instanceof Literal)
                && (this.conclusion != null && this.premise != null))
        {
            tempTerms.putAll(this.premise.setTerms(tempTerms));
            tempTerms.putAll(this.conclusion.setTerms(tempTerms));
        }

        return tempTerms;
    }
}