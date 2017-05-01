package info.ups.fr.puzzlegame_template;
/**
 * Created by Younes on 23/03/2015.
 */
public class PuzzleScore
{
	private int fois;
	private int meilleurScor;
	private boolean NouveauBestScore;

	public PuzzleScore(int fois, int meilleurScor, boolean NouveauBestScore)
	{
		this.fois = fois;
		this.meilleurScor = meilleurScor;
		this.NouveauBestScore = NouveauBestScore;
	}

	public int getPlays()
	{
		return fois;
	}

	public int getBest()
	{
		return meilleurScor;
	}

	public boolean isNewBest()
	{
		return NouveauBestScore;
	}
}