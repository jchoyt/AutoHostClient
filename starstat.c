#include <stdio.h>
#include <ctype.h>
#include <string.h>

#define dtXY   0
#define dtLog  1
#define dtHost 2
#define dtTurn 3
#define dtHist 4
#define dtRace 5
#define dtMax  6

#define rtBOF  8  // Begining Of File Record Type

typedef struct _rtbof
{
	char rgid[4];       // Magic number: "J3J3"
	long lidGame;        // Game ID
	
	unsigned short verInc   : 5,    // 1.04c
	               verMinor : 7,    // 1.04
	               verMajor : 4;    // 1.00
	               
	unsigned short turn;
	short iPlayer:5,
	      lSaltTime:11;  // Encryption salt
	      
	unsigned short dt         : 8, // File type dtXY, dtHost, dtLog, dtHist etc.
	               fDone      : 1, // Player has submitted this turn (dtLog only).
	               fInUse     : 1, // Host instance is using this file (dtHost, dtTurn).
	               fMulti     : 1, // Multiple turns in this file (dtTurn only).
	               fGameOver  : 1, // A winner has been declared
	               fShareware : 1, // The shareware version
	               unused     : 3;
} RTBOF;

typedef struct _hdr
{
	unsigned short cb : 10,  // Size of the record in bytes not counting this header.
	               rt :  6;  // Record Type (rtBOF etc)
} HDR;

char *szMagic = "J3J3";

char *rgszdt[7] = { "Universe Definition File", "Player Log File", "Host File", "Player Turn File",
                    "Player History File", "Race Definition File", "Unknown File" };
                    
int main(int argc, char *argv[])
{
	FILE *in;
	short w;
	HDR hdr;
	RTBOF rtbof;

	if(argc < 2)
		{
		fprintf(stderr, "StarStat filename\n\tDisplays status information about a Stars! game file.\n");
		return 1;
		}

	in = fopen(argv[1], "rb");
	if(!in)
		{
		fprintf(stderr, "StarStat: Unable to open '%s' for reading.\n", argv[1]);
		return 2;
		}

	if (fread(&hdr, 1, sizeof(HDR), in) != sizeof(HDR) || hdr.rt != rtBOF
	           || hdr.cb < sizeof(RTBOF) || fread(&rtbof, 1, sizeof(RTBOF), in) != sizeof(RTBOF)
	           || strncmp(szMagic, &rtbof.rgid[0], 4) || rtbof.dt >= dtMax)
		{
		fprintf(stderr, "StarStat: %s does not appear to be a Stars! file.\n", argv[1]);
		fclose(in);
		return 3;
		}

	printf("Stars! Version: %d.%02d%c ", rtbof.verMajor,
		            rtbof.verMinor, !rtbof.verInc ? ' ' : ('a' - 1 + rtbof.verInc));
	printf("%s", rgszdt[rtbof.dt]);
	if (rtbof.fInUse)
		printf(" (In Use)");
	printf("\n");
	
	printf("Unique Game Id Number: %08lx.%d\n", (unsigned long) rtbof.lidGame, (unsigned)rtbof.unused);

	switch (rtbof.dt)
		{
	case dtLog:
	case dtHost:
	case dtTurn:
		printf("Game Year: %u", rtbof.turn+2400);
		if (rtbof.dt == dtTurn && rtbof.fMulti)
			{
			fseek(in, -2, SEEK_END);
			fread(&w, 1, 2, in);
			printf(" to %u", w + 2400);
			}
		
		if (rtbof.fGameOver)
			printf(" - Game Over");
		printf("\n");

		if (rtbof.iPlayer != -1)
			{
			printf("Player: %d", rtbof.iPlayer+1);
			if (rtbof.fShareware)
				printf(" - Shareware");
			if (rtbof.dt == dtLog && rtbof.fDone)
				printf(" - Submitted");			
			printf("\n");
			}
		break;
		}
	       
	fclose(in);
	return 0;
}

