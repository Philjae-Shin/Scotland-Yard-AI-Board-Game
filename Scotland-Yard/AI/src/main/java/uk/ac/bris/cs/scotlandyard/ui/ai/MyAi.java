package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;


import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;

public class MyAi implements Ai {

	@Nonnull @Override public String name() { return "Name me!"; }

	@Nonnull @Override public Move pickMove(
			@Nonnull Board board,
			Pair<Long, TimeUnit> timeoutPair) {
		// returns a random move, replace with your own implementation
		var moves = board.getAvailableMoves().asList();
		var graph=board.getSetup().graph;int n=graph.nodes().size();
		ArrayList<Integer>dist= new ArrayList<Integer>(n);
		for(int i=0;i<n;i++){
			dist.set(i,2000000000);
		}
		int ans=-1; long cur= 0;
		for(int i=0;i<moves.size();i++){
			Move m=moves.get(i);
			Integer dest = m.accept(new Move.Visitor<Integer>(){
				@Override public Integer visit(Move.SingleMove singleMove){
					return singleMove.destination;
				}
				@Override public Integer visit(Move.DoubleMove doubleMove){
					return doubleMove.destination2;
				}
			});
            if(dijkista(dest,board)>cur){
				cur=dijkista(dest,board);
				ans=i;
			}
		}

		return moves.get(ans);
	}
	public Integer dijkista(Integer v,Board board){
		var graph=board.getSetup().graph;int n=graph.nodes().size(); Integer ans=0;
		ArrayList<Integer>dist= new ArrayList<Integer>(n);
		for(int i=0;i<n;i++){
			dist.set(i,2000000000);
		}
		PriorityQueue<Pair<Integer,Integer>>pq= new PriorityQueue<Pair<Integer,Integer>>();
		pq.add(new Pair<Integer,Integer>(v,0));
		while(pq.size()>0){
            Pair<Integer,Integer>t=pq.peek();
			pq.poll();
			for(Integer ad:graph.adjacentNodes(t.right())){
                 if(dist.get(t.right())+1<dist.get(ad)){
					 dist.set(ad,dist.get(t.right())+1);
					 pq.add(new Pair<Integer,Integer>(dist.get(t.right())+1,ad));
				 }
			}
		}
		for(Piece p: board.getPlayers()){
			if(board.getDetectiveLocation( Piece.Detective.WHITE).isPresent()){
				ans+=dist.get(board.getDetectiveLocation( Piece.Detective.WHITE).get());
			}
			if(board.getDetectiveLocation( Piece.Detective.BLUE).isPresent()) {
				ans += dist.get(board.getDetectiveLocation(Piece.Detective.BLUE).get());
			}
			if(board.getDetectiveLocation( Piece.Detective.GREEN).isPresent()) {
				ans += dist.get(board.getDetectiveLocation(Piece.Detective.GREEN).get());
			}
			if(board.getDetectiveLocation( Piece.Detective.RED).isPresent()) {
				ans += dist.get(board.getDetectiveLocation(Piece.Detective.RED).get());
			}
			if(board.getDetectiveLocation( Piece.Detective.YELLOW).isPresent()) {
				ans += dist.get(board.getDetectiveLocation(Piece.Detective.YELLOW).get());
			}
		}
		return ans;
	}
}
