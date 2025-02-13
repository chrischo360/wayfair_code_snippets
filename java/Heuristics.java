package src.pa2.heuristics;

import java.util.ArrayList;
import java.util.List;

import edu.bu.chess.game.move.CaptureMove;
import edu.bu.chess.game.move.Move;
import edu.bu.chess.game.piece.Piece;
import edu.bu.chess.game.piece.PieceType;
import edu.bu.chess.game.player.Player;
import edu.bu.chess.game.player.PlayerType;
// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;
import edu.bu.chess.utils.Coordinate;
import edu.cwru.sepia.util.Direction;
// JAVA PROJECT IMPORTS
import src.pa2.heuristics.DefaultHeuristics;
import java.lang.Math;

public class CustomHeuristics extends Object {

    /**
     * Get the max player from a node
     * @param node
     * @return
     */
    public static Player getMaxPlayer(DFSTreeNode node) {
        return node.getMaxPlayer();
    }

    /**
     * Get the min player from a node
     * @param node
     * @return
     */
    public static Player getMinPlayer(DFSTreeNode node) {
        return CustomHeuristics.getMaxPlayer(node).equals(node.getGame().getCurrentPlayer()) 
            ? node.getGame().getOtherPlayer() 
            : node.getGame().getCurrentPlayer();
    }

    public static class OffensiveHeuristics extends Object {

        public static int getNumberOfPiecesMaxPlayerIsThreatening(DFSTreeNode node) {
            int numPiecesMaxPlayerIsThreatening = 0;
            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node))) {
                numPiecesMaxPlayerIsThreatening += piece.getAllCaptureMoves(node.getGame()).size();
            }
            return numPiecesMaxPlayerIsThreatening;
        }

        public static int getTotalMaterialCostsDifference(DFSTreeNode node) {
            // return total material cost of all pieces - min players pieces
            int totalMaxPlayerMaterialCost = 0;
            int totalMinPlayerMaterialCost = 0;

            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node))) {
                totalMaxPlayerMaterialCost += Piece.getPointValue(piece.getType());
            }

            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node))) {
                totalMinPlayerMaterialCost += Piece.getPointValue(piece.getType());
            }

            return totalMaxPlayerMaterialCost - totalMinPlayerMaterialCost;
        }

        public static int getTotalNumberOfMoves(DFSTreeNode node) {
            // return total number of moves available
            int numberOfMovesAvailable = 0;

            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node))) {
                numberOfMovesAvailable += piece.getAllMoves(node.getGame()).size();
            }

            return numberOfMovesAvailable;
        }

        public static int getCenterControlValue(DFSTreeNode node) {
            // return the value of the pieces that are in the center (e4, d4, e5, and d5)
            int centerMaterialValue = 0;

            List<Coordinate> centerCoordinates = new ArrayList<Coordinate>() {{
                add(new Coordinate(4, 4));
                add(new Coordinate(5, 4));
                add(new Coordinate(4, 5));
                add(new Coordinate(5, 5));
            }};

            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node))) {
                Coordinate pieceCoord = node.getGame().getCurrentPosition(piece);
                for (Coordinate centerCoord : centerCoordinates) {
                    if (pieceCoord.getXPosition() == centerCoord.getXPosition() && pieceCoord.getYPosition() == centerCoord.getYPosition()) {
                        centerMaterialValue += Piece.getPointValue(piece.getType());
                    }
                }
            }

            return centerMaterialValue;
        }

        public static int getPawnStructureValue(DFSTreeNode node) {
            // more pawns that are connected = better
            int numConnectedPawns = 0;

            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.PAWN)) {
                Coordinate pawnPosition = node.getGame().getCurrentPosition(piece);
                for (Direction direction : Direction.values()) {
                    Coordinate neighborPosition = pawnPosition.getNeighbor(direction);
                    if (node.getGame().getBoard().isInbounds(neighborPosition) && node.getGame().getBoard().isPositionOccupied(neighborPosition)) {
                        Piece neighborPiece = node.getGame().getBoard().getPieceAtPosition(neighborPosition);
                        if (neighborPiece.getType() == PieceType.PAWN) {
                            numConnectedPawns += 1;
                        }
                    }
                }
            }

            return numConnectedPawns / 2;
        }

        public static int getPawnAdvancementValue(DFSTreeNode node) {
            // more pieces that are closer to the end of the board is better
            int pawnAdvancementValue = 0;

            int distance = 7;

            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.PAWN)) {
                Coordinate pawnPosition = node.getGame().getCurrentPosition(piece);
                int pawnYValue = pawnPosition.getYPosition();
                if (CustomHeuristics.getMaxPlayer(node).getPlayerType() == PlayerType.WHITE) {
                    pawnAdvancementValue += distance - (Math.abs(pawnYValue - 1));
                } else {
                    pawnAdvancementValue += distance - (Math.abs(8 - pawnYValue));
                }
            }

            return pawnAdvancementValue;
        }

        public static int getNumberOfPiecesThatHaveCheckOnMinKing(DFSTreeNode node) {
            // return number of pieces that have check on opposite king. more pieces is better
            int numberOfPiecesThatHaveCheck = 0;
            Piece kingPiece = node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node), PieceType.KING).iterator().next();

            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node))) {
                List<Move> captureMoves = piece.getAllCaptureMoves(node.getGame());

                for (Move captureMove : captureMoves) {
                    CaptureMove captureMove2 = (CaptureMove) captureMove;
                    if (captureMove2.getAttackingPieceID() == kingPiece.getPieceID()) {
                        numberOfPiecesThatHaveCheck += 1;
                    }
                }
            }

            return numberOfPiecesThatHaveCheck;
        }
    }

    public static class DefensiveHeuristics extends Object {
        public static int getNumberOfMaxPlayersAlivePieces(DFSTreeNode node) {
            int numMaxPlayersPiecesAlive = 0;
            for (PieceType pieceType : PieceType.values()) {
                numMaxPlayersPiecesAlive += node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMaxPlayer(node), pieceType);
            }
            return numMaxPlayersPiecesAlive;
        }

        public static int getNumberOfMinPlayersAlivePieces(DFSTreeNode node) {
            int numMaxPlayersPiecesAlive = 0;
            for (PieceType pieceType : PieceType.values()) {
                numMaxPlayersPiecesAlive += node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMinPlayer(node), pieceType);
            }
            return numMaxPlayersPiecesAlive;
        }

        public static int getClampedPieceValueTotalSurroundingMaxPlayersKing(DFSTreeNode node) {
            // what is the state of the pieces next to the king? add up the values of the neighboring pieces
            // positive value for friendly pieces and negative value for enemy pieces (will clamp at 0)
            int maxPlayerKingSurroundingPiecesValueTotal = 0;

            Piece kingPiece = node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.KING).iterator().next();
            Coordinate kingPosition = node.getGame().getCurrentPosition(kingPiece);
            for (Direction direction : Direction.values()) {
                Coordinate neighborPosition = kingPosition.getNeighbor(direction);
                if (node.getGame().getBoard().isInbounds(neighborPosition) && node.getGame().getBoard().isPositionOccupied(neighborPosition)) {
                    Piece piece = node.getGame().getBoard().getPieceAtPosition(neighborPosition);
                    int pieceValue = Piece.getPointValue(piece.getType());
                    if (piece != null && kingPiece.isEnemyPiece(piece)) {
                        maxPlayerKingSurroundingPiecesValueTotal -= pieceValue;
                    } else if (piece != null && !kingPiece.isEnemyPiece(piece)) {
                        maxPlayerKingSurroundingPiecesValueTotal += pieceValue;
                    }
                }
            }
            // kingSurroundingPiecesValueTotal cannot be < 0 b/c the utility of losing a game is 0, so all of our utility values should be at least 0
            maxPlayerKingSurroundingPiecesValueTotal = Math.max(maxPlayerKingSurroundingPiecesValueTotal, 0);
            return maxPlayerKingSurroundingPiecesValueTotal;
        }

        public static int getNumberOfPiecesThreateningMaxPlayer(DFSTreeNode node) {
            // how many pieces are threatening us?
            int numPiecesThreateningMaxPlayer = 0;
            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node))) {
                numPiecesThreateningMaxPlayer += piece.getAllCaptureMoves(node.getGame()).size();
            }
            return numPiecesThreateningMaxPlayer;
        }

        public static int getNumberOfPiecesThatHaveCheck(DFSTreeNode node) {
            // return number of pieces that have check on our king.
            int numberOfPiecesThatHaveCheck = 0;
            Piece kingPiece = node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.KING).iterator().next();

            for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node))) {
                List<Move> captureMoves = piece.getAllCaptureMoves(node.getGame());

                for (Move captureMove : captureMoves) {
                    CaptureMove captureMove2 = (CaptureMove) captureMove;
                    if (captureMove2.getAttackingPieceID() == kingPiece.getPieceID()) {
                        numberOfPiecesThatHaveCheck += 1;
                    }
                }
            }

            return numberOfPiecesThatHaveCheck;
        }
    }

    public static double getOffensiveMaxPlayerHeuristicValue(DFSTreeNode node) {
        int totalMaterialCostsDifference = OffensiveHeuristics.getTotalMaterialCostsDifference(node);
        int numberOfPiecesMaxPlayerIsThreatening = OffensiveHeuristics.getNumberOfPiecesMaxPlayerIsThreatening(node);
        int totalNumberOfMoves = OffensiveHeuristics.getTotalNumberOfMoves(node);
        int centerMaterialValue = OffensiveHeuristics.getCenterControlValue(node);
        // int pawnStructure =  OffensiveHeuristics.getPawnStructureValue(node);
        int pawnAdvancement = OffensiveHeuristics.getPawnAdvancementValue(node);
        int numberOfPiecesThatHaveCheck = OffensiveHeuristics.getNumberOfPiecesThatHaveCheckOnMinKing(node);

        return (totalMaterialCostsDifference * 50) + numberOfPiecesMaxPlayerIsThreatening + totalNumberOfMoves + centerMaterialValue + pawnAdvancement + (numberOfPiecesThatHaveCheck * 100);
    }

    public static double getDefensiveMaxPlayerHeuristicValue(DFSTreeNode node) {
        int numPiecesAlive = DefensiveHeuristics.getNumberOfMaxPlayersAlivePieces(node);
        int clampedPieceValueTotalSurroundingMaxPlayersKing = DefensiveHeuristics.getClampedPieceValueTotalSurroundingMaxPlayersKing(node);
        int numberOfPiecesThreateningMaxPlayer = DefensiveHeuristics.getNumberOfPiecesThreateningMaxPlayer(node);
        int numberOfPiecesThatHaveCheck = DefensiveHeuristics.getNumberOfPiecesThatHaveCheck(node);

        return clampedPieceValueTotalSurroundingMaxPlayersKing + numberOfPiecesThreateningMaxPlayer + numPiecesAlive + (numberOfPiecesThatHaveCheck * -100);
    }

    /**
     * TODO: implement me! The heuristics that I wrote are useful, but not very good for a good chessbot.
     * Please use this class to add your heuristics here! I recommend taking a look at the ones I provided for you
     * in DefaultHeuristics.java (which is in the same directory as this file)
     */
    public static double getMaxPlayerHeuristicValue(DFSTreeNode node) {
        double offenseHeuristicValue = CustomHeuristics.getOffensiveMaxPlayerHeuristicValue(node);
        double defenseHeuristicValue = CustomHeuristics.getDefensiveMaxPlayerHeuristicValue(node);
        return offenseHeuristicValue + defenseHeuristicValue;
    }
}

