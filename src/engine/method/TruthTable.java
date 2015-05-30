package engine.method;

import java.util.HashMap;
import java.util.LinkedList;

import logic.Literal;
import logic.Sentence;

public class TruthTable implements SolveMethod
{
    private HashMap<String, Literal> literals;
    private LinkedList<Sentence> sentences;
    private String ask;

    public TruthTable(LinkedList<Sentence> clauseList, HashMap<String, Literal> literals, String ask)
    {
        this.literals = literals;
        this.sentences = clauseList;
        this.ask = ask;
    }

    @Override
    public void solve()
    {
        LinkedList<Literal> orderTerms = new LinkedList<Literal>();
        int i = 0, truths = 0;
        int[] masks = new int[this.literals.size()];

        for(Literal l : this.literals.values())
        {
            masks[i] = 1 << i;
            l.setValue(false);
            orderTerms.addFirst(l);
            i++;
        }

        for(int j = 0; j < Math.pow(2, orderTerms.size()); j++)
        {
            boolean failed = false;
            for(int k = 0; k < orderTerms.size(); k++)
            {
                if((j & masks[k]) == masks[k])
                {
                    Literal l = orderTerms.get(k);
                    l.setValue(true);
                }
            }

            for(Sentence s : this.sentences)
            {
                if(!s.evaluate())
                {
                    failed = true;
                    break;
                }
            }

            if(!failed)
            {
                if(this.literals.get(this.ask).evaluate())
                {
                    truths++;
                }
                else
                {
                    System.out.println("NO");
                    return;
                }
            }

            for(Literal lit : orderTerms)
            {
                lit.setValue(false);
            }
        }

        System.out.println("YES: " + truths);
    }
}
