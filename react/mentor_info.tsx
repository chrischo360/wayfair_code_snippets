"use client";
import { AvatarComponent } from "@/components/ui/avatar";
import { ExtendedMentorData } from "@/utils/supabase/mentor.types";
import { Badge } from "@/components/ui/badge";
import { useEffect, useState } from "react";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { MentorVideo } from ".";
import { useToast } from "../ui/use-toast";

interface Props {
    name: ExtendedMentorData["name"];
    school: ExtendedMentorData["school"];
    image: ExtendedMentorData["image"];
    school_year: ExtendedMentorData["school_year"];
    gpa: ExtendedMentorData["gpa"];
    sat: ExtendedMentorData["sat"];
    act: ExtendedMentorData["act"];
    videoId: ExtendedMentorData["video_id"];
}

export const MentorInfo = ({
    name,
    school,
    image,
    school_year,
    gpa,
    sat,
    act,
    videoId,
}: Props) => {
    const [isClicked, setIsClicked] = useState(false);
    const { toast } = useToast();

    const handleClick = () => {
        setIsClicked(true);
    };

    useEffect(() => {
        toast({
            variant: "success",
            title: "Click on a mentor's profile picture to see a video!",
        });
    }, []);

    return (
        <div className="flex flex-col grid-in-info">
            <div className="flex gap-1">
                {videoId ? (
                    <Sheet>
                        <SheetTrigger asChild>
                            <div
                                className={
                                    isClicked
                                        ? "rounded-full ring ring-pink-300 ring-offset-1 hover:animate-none"
                                        : "z-0 animate-pulse-out rounded-full ring ring-pink-300 ring-offset-1 hover:animate-none"
                                }
                            >
                                <button onClick={handleClick}>
                                    <AvatarComponent
                                        name={name}
                                        src={
                                            image ??
                                            "https://bqsdxkfoamywyazyrmek.supabase.co/storage/v1/object/public/images/gray-screen.jpeg?t=2024-02-18T01%3A06%3A58.288Z"
                                        }
                                        fallbackName={name}
                                        size={"lg"}
                                    />
                                </button>
                            </div>
                        </SheetTrigger>
                        <SheetContent
                            side={"bottom"}
                            className="duration-400 max-h-[85%] bg-gray-50 p-0 shadow-lg transition-all"
                        >
                            <MentorVideo videoId={videoId} />
                        </SheetContent>
                    </Sheet>
                ) : (
                    <AvatarComponent
                        name={name}
                        src={
                            image ??
                            "https://bqsdxkfoamywyazyrmek.supabase.co/storage/v1/object/public/images/gray-screen.jpeg?t=2024-02-18T01%3A06%3A58.288Z"
                        }
                        fallbackName={name}
                        size={"lg"}
                    />
                )}
                <div className="pl-4 pt-2">
                    <p className="text-lg font-bold xl:text-2xl">{name}</p>
                    {school[0] ? (
                        <div className="flex font-normal">
                            <div className="mr-2 text-gray-500">
                                {school[0].name + " "}
                                {school_year ? school_year : ""}
                            </div>
                            <AvatarComponent
                                key={school[0].school_id}
                                src={
                                    school[0].image ??
                                    "https://bqsdxkfoamywyazyrmek.supabase.co/storage/v1/object/public/images/gray-screen.jpeg?t=2024-02-18T01%3A06%3A58.288Z"
                                }
                                name={school[0].name}
                                fallbackName={school[0].name}
                                size={"xs"}
                            />
                        </div>
                    ) : (
                        <></>
                    )}
                </div>
                <div className="mx-2 hidden flex-col md:flex">
                    {gpa ? (
                        <Badge
                            className="texl-lg rounded-xl border-black py-0 font-semibold text-black"
                            variant="outline"
                        >
                            GPA: {gpa}
                        </Badge>
                    ) : (
                        <></>
                    )}
                    <div className="flex gap-1">
                        {sat && sat != -1 ? (
                            <Badge
                                variant="outline"
                                className="texl-lg rounded-xl border-black py-0 font-semibold text-black"
                            >
                                SAT: {sat}
                            </Badge>
                        ) : (
                            <></>
                        )}
                        {act && act != -1 ? (
                            <Badge
                                variant="outline"
                                className="texl-xl rounded-xl border-black py-0 font-semibold text-black"
                            >
                                ACT: {act}
                            </Badge>
                        ) : (
                            <></>
                        )}
                    </div>
                </div>
            </div>
            <div className="flex grow flex-wrap gap-1 pt-2 md:hidden">
                {gpa ? (
                    <Badge
                        className="texl-lg rounded-xl border-black py-0 font-semibold text-black"
                        variant="outline"
                    >
                        GPA: {gpa}
                    </Badge>
                ) : (
                    <></>
                )}
                <div className="flex gap-1">
                    {sat && sat != -1 ? (
                        <Badge
                            variant="outline"
                            className="texl-lg rounded-xl border-black py-0 font-semibold text-black"
                        >
                            SAT: {sat}
                        </Badge>
                    ) : (
                        <></>
                    )}
                    {act && act != -1 ? (
                        <Badge
                            variant="outline"
                            className="texl-lg rounded-xl border-black py-0 font-semibold text-black"
                        >
                            ACT: {act}
                        </Badge>
                    ) : (
                        <></>
                    )}
                </div>
            </div>
        </div>
    );
};
